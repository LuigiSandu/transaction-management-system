package project.transaction.management.system.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.transaction.TransactionRequestResource;
import project.transaction.management.system.api.resource.transaction.TransactionResponseResource;
import project.transaction.management.system.config.JWTGenerator;
import project.transaction.management.system.dao.entity.AccountEntity;
import project.transaction.management.system.dao.entity.TransactionEntity;
import project.transaction.management.system.dao.repository.AccountRepository;
import project.transaction.management.system.dao.repository.TransactionReposittory;
import project.transaction.management.system.dao.repository.UserRepository;
import project.transaction.management.system.mapper.TransactionMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionMapper mapper;
    private final TransactionReposittory transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final JWTGenerator jwtGenerator;

    @Transactional
    public TransactionResponseResource createTransaction(TransactionRequestResource request, String authorizationHeader) {
        final AccountEntity accountEntity = validateAccountExistsAndRetrieveIt(request, authorizationHeader);

        TransactionEntity transactionEntity = null;

        switch (request.getTransactionType().toUpperCase()) {
            case "DEPOSIT":
                processDeposit(accountEntity, request.getAmount());
                transactionEntity = TransactionEntity.builder()
                        .sourceAccount(accountEntity)
                        .transactionType("DEPOSIT")
                        .amount(request.getAmount())
                        .description(request.getDescription())
                        .build();
                break;

            case "WITHDRAWAL":
                processWithdrawal(accountEntity, request.getAmount());
                transactionEntity = TransactionEntity.builder()
                        .sourceAccount(accountEntity)
                        .transactionType("WITHDRAWAL")
                        .amount(request.getAmount())
                        .description(request.getDescription())
                        .build();
                break;

            case "TRANSFER":
                AccountEntity targetAccountEntity = getAccountByNumber(request.getTargetAccountNumber());
                transactionEntity = TransactionEntity.builder()
                        .sourceAccount(accountEntity)
                        .targetAccount(targetAccountEntity)
                        .transactionType("TRANSFER")
                        .amount(request.getAmount())
                        .description(request.getDescription())
                        .build();
                return processTransfer(transactionEntity, accountEntity, targetAccountEntity);

            default:
                throw new IllegalArgumentException("Invalid transaction type: " + request.getTransactionType());
        }

        transactionEntity = transactionRepository.save(transactionEntity);
        accountRepository.save(accountEntity);

        return mapper.fromEntity(transactionEntity);
    }

    public List<TransactionResponseResource> getAllTransactionsByUserId(String authorizationHeader) {
        String userId = jwtGenerator.getUserIdFromToken(authorizationHeader.substring(7));
        log.debug("Received request to get all transactions for user with ID: {}", userId);
        validateUserExistsById(Long.parseLong(userId));
        List<TransactionEntity> transactions = transactionRepository.findBySourceAccount_User_Id(Long.parseLong(userId));
        log.info("Successfully retrieved {} transactions for user ID: {}", transactions.size(), userId);
        return transactions.stream()
                .map(mapper::fromEntity)
                .collect(Collectors.toList());
    }

    private AccountEntity validateAccountExistsAndRetrieveIt(TransactionRequestResource request, String authorizationHeader) {
        String userId = jwtGenerator.getUserIdFromToken(authorizationHeader.substring(7));

        if (request.getTargetAccountNumber() != null &&
                Objects.equals(request.getSourceAccountNumber(), request.getTargetAccountNumber())) {
            throw new IllegalArgumentException("Target account can't be the same as source account.");
        }

        return getAccountByNumberAndUserId(request.getSourceAccountNumber(), Long.parseLong(userId));
    }


    private void validateUserExistsById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }


    private void processDeposit(AccountEntity accountEntity, Double amount) {
        if (!"CHECKING".equalsIgnoreCase(accountEntity.getAccountType())) {
            throw new IllegalArgumentException("Deposits can only be made to checking accounts.");
        }
        accountEntity.setBalance(accountEntity.getBalance() + amount); // Add the deposit amount to the balance
        log.info("Deposited {} to account {}", amount, accountEntity.getAccountNumber());
    }


    private void processWithdrawal(AccountEntity accountEntity, Double amount) {
        if (!"CHECKING".equalsIgnoreCase(accountEntity.getAccountType())) {
            throw new IllegalArgumentException("Withdrawals can only be made from checking accounts.");
        }
        if (accountEntity.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal.");
        }
        accountEntity.setBalance(accountEntity.getBalance() - amount); // Subtract the withdrawal amount from the balance
        log.info("Withdrew {} from account {}", amount, accountEntity.getAccountNumber());
    }

    private TransactionResponseResource processTransfer(TransactionEntity transactionEntity, AccountEntity sourceAccount, AccountEntity targetAccount) {
        validateTransferConditions(sourceAccount, targetAccount);
        validateTransferAmount(transactionEntity.getAmount());

        if (sourceAccount.getBalance() < transactionEntity.getAmount()) {
            throw new IllegalArgumentException("Insufficient funds for transfer.");
        }


        return executeTransfer(sourceAccount, targetAccount, transactionEntity.getAmount());
    }

    private void validateTransferConditions(AccountEntity sourceAccount, AccountEntity targetAccount) {
        if (sourceAccount.getUser().equals(targetAccount.getUser())) {
            log.debug("Transferring between accounts of the same user: {} -> {}", sourceAccount.getAccountNumber(), targetAccount.getAccountNumber());
        }
        else if ("CHECKING".equalsIgnoreCase(sourceAccount.getAccountType()) && "CHECKING".equalsIgnoreCase(targetAccount.getAccountType())) {
            log.debug("Transferring from one user's checking account to another user's checking account.");
        } else {
            throw new IllegalArgumentException("Transfers can only happen between checking accounts.");
        }
    }

    private void validateTransferAmount(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
    }

    private TransactionResponseResource executeTransfer(AccountEntity sourceAccount, AccountEntity targetAccount, Double amount) {
        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        targetAccount.setBalance(targetAccount.getBalance() + amount);

        final TransactionEntity sourceTransaction = TransactionEntity.builder()
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .transactionType("TRANSFER")
                .amount(amount)
                .description("Transfer to " + targetAccount.getAccountNumber())
                .build();
        transactionRepository.save(sourceTransaction);

        final TransactionEntity targetTransaction = TransactionEntity.builder()
                .sourceAccount(targetAccount)
                .targetAccount(sourceAccount) // log the source account as the source in the description
                .transactionType("TRANSFER")
                .amount(amount)
                .description("Transfer from " + sourceAccount.getAccountNumber())
                .build();
        transactionRepository.save(targetTransaction);

        log.info("Transferred {} from account {} to account {}", amount, sourceAccount.getAccountNumber(), targetAccount.getAccountNumber());

        return TransactionResponseResource.builder()
                .sourceAccountNumber(sourceAccount.getAccountNumber())
                .description(sourceTransaction.getDescription())
                .amount(amount)
                .transactionType("TRANSFER")
                .targetAccountNumber(targetAccount.getAccountNumber())
                .timestamp(sourceTransaction.getTimestamp())
                .id(sourceTransaction.getId())
                .build();
    }

    private AccountEntity getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));
    }

    private AccountEntity getAccountByNumberAndUserId(String accountNumber, Long userId) {
        return accountRepository.findByAccountNumberAndUserId(accountNumber, userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber + " for userId: " + userId));
    }
}