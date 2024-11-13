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
    public TransactionResponseResource createTransaction(TransactionRequestResource request, String authenticatedUsername) {
        // Validate user
        validateUserExistsByUsername(authenticatedUsername);

        // Fetch the source account
        final AccountEntity accountEntity = getAccountByNumber(request.getSourceAccountNumber());

        // Define the transaction entity
        TransactionEntity transactionEntity = null;

        // Process the transaction based on type
        switch (request.getTransactionType().toUpperCase()) {
            case "DEPOSIT":
                processDeposit(accountEntity, request.getAmount());
                transactionEntity = TransactionEntity.builder()
                        .account(accountEntity)  // Set the source account for the deposit
                        .transactionType("DEPOSIT")
                        .amount(request.getAmount())
                        .description(request.getDescription())
                        .build();
                break;

            case "WITHDRAWAL":
                processWithdrawal(accountEntity, request.getAmount());
                transactionEntity = TransactionEntity.builder()
                        .account(accountEntity)  // Set the source account for the withdrawal
                        .transactionType("WITHDRAWAL")
                        .amount(request.getAmount())
                        .description(request.getDescription())
                        .build();
                break;

            case "TRANSFER":
                AccountEntity targetAccountEntity = getAccountByNumber(request.getTargetAccountNumber());
                transactionEntity = TransactionEntity.builder()
                        .account(accountEntity)   // Set the source account for the transfer
                        .targetAccount(targetAccountEntity) // Set the target account for the transfer
                        .transactionType("TRANSFER")
                        .amount(request.getAmount())
                        .description(request.getDescription())
                        .build();
                return processTransfer(transactionEntity, accountEntity, targetAccountEntity); // Directly return the transfer response

            default:
                throw new IllegalArgumentException("Invalid transaction type: " + request.getTransactionType());
        }

        // Save the transaction and update the account balance for DEPOSIT and WITHDRAWAL
        transactionEntity = transactionRepository.save(transactionEntity);
        accountRepository.save(accountEntity); // Ensure the account is saved with updated balance

        // Return the response resource
        return mapper.fromEntity(transactionEntity);
    }

    public List<TransactionResponseResource> getAllTransactionsByUserId(String authorizationHeader) {
        String userId = jwtGenerator.getUserIdFromToken(authorizationHeader.substring(7));
        log.debug("Received request to get all transactions for user with ID: {}", userId); // Log the incoming request
        validateUserExistsById(Long.parseLong(userId));
        List<TransactionEntity> transactions = transactionRepository.findByAccount_User_Id(Long.parseLong(userId));
        log.info("Successfully retrieved {} transactions for user ID: {}", transactions.size(), userId);
        return transactions.stream()
                .map(mapper::fromEntity)
                .collect(Collectors.toList());
    }

    private void validateUserExistsById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }

    // Process the deposit (add the amount to the balance)
    private void processDeposit(AccountEntity accountEntity, Double amount) {
        if (!"CHECKING".equalsIgnoreCase(accountEntity.getAccountType())) {
            throw new IllegalArgumentException("Deposits can only be made to checking accounts.");
        }
        accountEntity.setBalance(accountEntity.getBalance() + amount); // Add the deposit amount to the balance
        log.info("Deposited {} to account {}", amount, accountEntity.getAccountNumber());
    }

    // Process the withdrawal (subtract the amount from the balance)
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
        validateTransferAmount(transactionEntity.getAmount());

        if (sourceAccount.getBalance() < transactionEntity.getAmount()) {
            throw new IllegalArgumentException("Insufficient funds for transfer.");
        }

        // Perform the transfer and create transaction records for both accounts
        return executeTransfer(sourceAccount, targetAccount, transactionEntity.getAmount());
    }

    private void validateTransferAmount(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
    }

    private TransactionResponseResource executeTransfer(AccountEntity sourceAccount, AccountEntity targetAccount, Double amount) {
        // Update the balances
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

    private AccountEntity getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));
    }

    private void validateUserExistsByUsername(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
    }
}