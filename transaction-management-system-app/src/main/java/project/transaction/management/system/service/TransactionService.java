package project.transaction.management.system.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.transaction.TransactionRequestResource;
import project.transaction.management.system.api.resource.transaction.TransactionResponseResource;
import project.transaction.management.system.dao.entity.AccountEntity;
import project.transaction.management.system.dao.entity.TransactionEntity;
import project.transaction.management.system.dao.repository.AccountRepository;
import project.transaction.management.system.dao.repository.TransactionReposittory;
import project.transaction.management.system.mapper.TransactionMapper;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionMapper mapper;
    private final TransactionReposittory transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionResponseResource createTransaction(TransactionRequestResource request) {
        // Check if the account exists
        AccountEntity accountEntity = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + request.getAccountNumber()));

        // Verify sufficient funds if the transaction type is a withdrawal
        if ("WITHDRAWAL".equalsIgnoreCase(request.getTransactionType()) &&
                accountEntity.getBalance() < request.getAmount()) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal.");
        }

        // Create a new TransactionEntity from the request
        TransactionEntity transactionEntity = mapper.toEntity(request);

        // Set the associated account for the transaction
        transactionEntity.setAccount(accountEntity);

        // Optionally set the timestamp and initial status (the timestamp is set automatically in the entity)
        // transactionEntity.setTimestamp(LocalDateTime.now()); // No need to set manually if using @PrePersist in the entity

        // Save the transaction to the repository
        TransactionEntity savedTransaction = transactionRepository.save(transactionEntity);

        // Update account balance if necessary (for withdrawal, for instance)
        if ("WITHDRAWAL".equalsIgnoreCase(request.getTransactionType())) {
            accountEntity.setBalance(accountEntity.getBalance() - request.getAmount());
            accountRepository.save(accountEntity);
        }

        // Map the saved entity to a response resource and return it
        return mapper.fromEntity(savedTransaction);
    }
}


