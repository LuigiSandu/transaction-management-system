package project.transaction.management.system.service.helper;

import project.transaction.management.system.api.resource.transaction.TransactionRequestResource;
import project.transaction.management.system.dao.entity.AccountEntity;
import project.transaction.management.system.dao.entity.TransactionEntity;
import project.transaction.management.system.dao.entity.UserEntity;

public class TransactionHelper {

    public static UserEntity createUser(Long userId, String username) {
        return UserEntity.builder()
                .id(userId)
                .username(username)
                .build();
    }

    public static AccountEntity createAccount(String accountNumber, double balance, String accountType, UserEntity user) {
        return AccountEntity.builder()
                .accountNumber(accountNumber)
                .balance(balance)
                .accountType(accountType)
                .user(user) // Assign the user
                .build();
    }

    public static TransactionEntity createTransaction(AccountEntity account, AccountEntity targetAccount, String transactionType, double amount, String description) {
        return TransactionEntity.builder()
                .account(account)
                .targetAccount(targetAccount)
                .transactionType(transactionType)
                .amount(amount)
                .description(description)
                .build();
    }

    public static TransactionRequestResource createTransactionRequest(String sourceAccountNumber, String targetAccountNumber, String transactionType, double amount, String description) {
        return TransactionRequestResource.builder()
                .sourceAccountNumber(sourceAccountNumber)
                .targetAccountNumber(targetAccountNumber)
                .transactionType(transactionType)
                .amount(amount)
                .description(description)
                .build();
    }
}
