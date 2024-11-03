package project.transaction.management.system.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.transaction.TransactionRequestResource;
import project.transaction.management.system.api.resource.transaction.TransactionResponseResource;
import project.transaction.management.system.dao.entity.AccountEntity;
import project.transaction.management.system.dao.entity.TransactionEntity;
import project.transaction.management.system.dao.repository.AccountRepository;
import project.transaction.management.system.dao.repository.TransactionReposittory;
import project.transaction.management.system.dao.repository.UserRepository;
import project.transaction.management.system.mapper.TransactionMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionMapper mapper;
    private final TransactionReposittory transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResponseResource createTransaction(TransactionRequestResource request) {
        AccountEntity accountEntity = getAccountByNumber(request.getSourceAccountNumber());

        // Remove transactionEntity creation for transfers
        switch (request.getTransactionType().toUpperCase()) {
            case "DEPOSIT":
                processDeposit(accountEntity, request.getAmount());
                break;
            case "WITHDRAWAL":
                processWithdrawal(accountEntity, request.getAmount());
                break;
            case "TRANSFER":
                return processTransfer(request, accountEntity); // Directly return the transfer response
            default:
                throw new IllegalArgumentException("Invalid transaction type: " + request.getTransactionType());
        }

        // Create a transaction entity for deposits and withdrawals
        TransactionEntity savedTransaction = transactionRepository.save(mapper.toEntity(request)); // Adjust this as necessary
        accountRepository.save(accountEntity);

        return mapper.fromEntity(savedTransaction);
    }

    public List<TransactionResponseResource> getAllTransactionsByUserId(Long userId) {
        validateUserExists(userId);
        List<TransactionEntity> transactions = transactionRepository.findByAccount_User_Id(userId);
        return transactions.stream()
                .map(mapper::fromEntity)
                .collect(Collectors.toList());
    }

    private AccountEntity getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));
    }

    private void processDeposit(AccountEntity accountEntity, Double amount) {
        if (!"CHECKING".equalsIgnoreCase(accountEntity.getAccountType())) {
            throw new IllegalArgumentException("Deposits can only be made to checking accounts.");
        }
        accountEntity.setBalance(accountEntity.getBalance() + amount);
        log.info("Deposited {} to account {}", amount, accountEntity.getAccountNumber());
    }

    private void processWithdrawal(AccountEntity accountEntity, Double amount) {
        if (!"CHECKING".equalsIgnoreCase(accountEntity.getAccountType())) {
            throw new IllegalArgumentException("Withdrawals can only be made from checking accounts.");
        }
        if (accountEntity.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal.");
        }
        accountEntity.setBalance(accountEntity.getBalance() - amount);
        log.info("Withdrew {} from account {}", amount, accountEntity.getAccountNumber());
    }

    private TransactionResponseResource processTransfer(TransactionRequestResource request, AccountEntity sourceAccount) {
        validateTransferAmount(request.getAmount());
        AccountEntity targetAccount = getAccountByNumber(request.getTargetAccountNumber());

        if (sourceAccount.getBalance() < request.getAmount()) {
            throw new IllegalArgumentException("Insufficient funds for transfer.");
        }

        if (!sourceAccount.getUser().getId().equals(targetAccount.getUser().getId())) {
            if (!"CHECKING".equalsIgnoreCase(sourceAccount.getAccountType()) ||
                    !"CHECKING".equalsIgnoreCase(targetAccount.getAccountType())) {
                throw new IllegalArgumentException("Cross-user transfers can only be done between checking accounts.");
            }
        }

        // Perform the transfer and create transaction records
        return executeTransfer(sourceAccount, targetAccount, request.getAmount());


    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }

    private void validateTransferAmount(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
    }

    private TransactionResponseResource executeTransfer(AccountEntity sourceAccount, AccountEntity targetAccount, Double amount) {
        // Update balances
        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        targetAccount.setBalance(targetAccount.getBalance() + amount);

        // Create and save transaction record for the source account
        TransactionEntity sourceTransaction = TransactionEntity.builder()
                .account(sourceAccount)
                .targetAccount(targetAccount)
                .transactionType("TRANSFER")
                .amount(amount)
                .description("Transfer to " + targetAccount.getAccountNumber())
                .build();
        transactionRepository.save(sourceTransaction);

        // Create and save transaction record for the target account
        TransactionEntity targetTransaction = TransactionEntity.builder()
                .account(targetAccount)
                .targetAccount(sourceAccount) // log the source account as the source in the description
                .transactionType("TRANSFER")
                .amount(amount)
                .description("Transfer from " + sourceAccount.getAccountNumber())
                .build();
        transactionRepository.save(targetTransaction);

        // Log the transfer operation
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
}
