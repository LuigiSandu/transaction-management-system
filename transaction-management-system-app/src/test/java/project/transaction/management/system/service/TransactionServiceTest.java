package project.transaction.management.system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.transaction.management.system.TransactionHelper;
import project.transaction.management.system.api.resource.transaction.TransactionRequestResource;
import project.transaction.management.system.api.resource.transaction.TransactionResponseResource;
import project.transaction.management.system.config.JWTGenerator;
import project.transaction.management.system.dao.entity.AccountEntity;
import project.transaction.management.system.dao.entity.TransactionEntity;
import project.transaction.management.system.dao.entity.UserEntity;
import project.transaction.management.system.dao.repository.AccountRepository;
import project.transaction.management.system.dao.repository.TransactionReposittory;
import project.transaction.management.system.dao.repository.UserRepository;
import project.transaction.management.system.mapper.TransactionMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static project.transaction.management.system.AbstractTest.AUTHORIZATION_HEADER;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionReposittory transactionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JWTGenerator jwtGenerator;
    @Mock
    private TransactionMapper transactionMapper;
    @InjectMocks
    private TransactionService transactionService;

    private UserEntity user;
    private AccountEntity accountEntity;
    private AccountEntity targetAccount;

    @BeforeEach
    void setUp() {
        user = TransactionHelper.createUser(1L, "testUser");
        accountEntity = TransactionHelper.createAccount("12345678", 500.0, "CHECKING", user);
        targetAccount = TransactionHelper.createAccount("87654321", 200.0, "CHECKING", user);
    }

    @Test
    void testCreateDepositTransaction() {
        final TransactionEntity transactionEntity = TransactionHelper.createTransaction(accountEntity, null, "DEPOSIT", 100.0, "Deposit into account");
        final TransactionRequestResource request = TransactionHelper.createTransactionRequest("12345678", null, "DEPOSIT", 100.0, "Deposit into account");

        when(jwtGenerator.getUserIdFromToken(AUTHORIZATION_HEADER.substring(7))).thenReturn("1");
        when(accountRepository.findByAccountNumberAndUserId("12345678", 1L)).thenReturn(Optional.of(accountEntity));
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(transactionEntity);
        when(transactionMapper.fromEntity(transactionEntity)).thenReturn(TransactionResponseResource.builder()
                .sourceAccountNumber("12345678")
                .amount(100.0)
                .transactionType("DEPOSIT")
                .description("Deposit into account")
                .build());

        final TransactionResponseResource response = transactionService.createTransaction(request, AUTHORIZATION_HEADER);

        assertNotNull(response);
        assertEquals("12345678", response.getSourceAccountNumber());
        assertEquals(100.0, response.getAmount());
        assertEquals("DEPOSIT", response.getTransactionType());
        assertEquals("Deposit into account", response.getDescription());
    }

    @Test
    void testCreateWithdrawalTransaction() {
        final TransactionEntity transactionEntity = TransactionHelper.createTransaction(accountEntity, null, "WITHDRAWAL", 100.0, "Withdrawal from account");
        final TransactionRequestResource request = TransactionHelper.createTransactionRequest("12345678", null, "WITHDRAWAL", 100.0, "Withdrawal from account");

        when(jwtGenerator.getUserIdFromToken(AUTHORIZATION_HEADER.substring(7))).thenReturn("1");
        when(accountRepository.findByAccountNumberAndUserId("12345678", 1L)).thenReturn(Optional.of(accountEntity));
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(transactionEntity);
        when(transactionMapper.fromEntity(transactionEntity)).thenReturn(TransactionResponseResource.builder()
                .sourceAccountNumber("12345678")
                .amount(100.0)
                .transactionType("WITHDRAWAL")
                .description("Withdrawal from account")
                .build());

        final TransactionResponseResource response = transactionService.createTransaction(request, AUTHORIZATION_HEADER);

        assertNotNull(response);
        assertEquals("12345678", response.getSourceAccountNumber());
        assertEquals(100.0, response.getAmount());
        assertEquals("WITHDRAWAL", response.getTransactionType());
        assertEquals("Withdrawal from account", response.getDescription());
    }

    @Test
    void testCreateTransferTransaction() {
        final TransactionEntity sourceTransaction = TransactionHelper.createTransaction(accountEntity, targetAccount, "TRANSFER", 100.0, "Transfer to 87654321");
        final TransactionEntity targetTransaction = TransactionHelper.createTransaction(targetAccount, accountEntity, "TRANSFER", 100.0, "Transfer from 12345678");
        final TransactionRequestResource request = TransactionHelper.createTransactionRequest("12345678", "87654321", "TRANSFER", 100.0, "Transfer to account");

        when(jwtGenerator.getUserIdFromToken(AUTHORIZATION_HEADER.substring(7))).thenReturn("1");
        when(accountRepository.findByAccountNumberAndUserId("12345678", 1L)).thenReturn(Optional.of(accountEntity));
        when(accountRepository.findByAccountNumber("87654321")).thenReturn(Optional.of(targetAccount));
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(sourceTransaction, targetTransaction);

        final TransactionResponseResource response = transactionService.createTransaction(request, AUTHORIZATION_HEADER);

        assertNotNull(response);
        assertEquals("12345678", response.getSourceAccountNumber());
        assertEquals("87654321", response.getTargetAccountNumber());
        assertEquals(100.0, response.getAmount());
        assertEquals("TRANSFER", response.getTransactionType());
        assertEquals("Transfer to 87654321", response.getDescription());
    }

    @Test
    void testCreateTransferTransaction_InsufficientFunds() {
        final TransactionRequestResource request = TransactionHelper.createTransactionRequest("12345678", "87654321", "TRANSFER", 600.0, "Transfer to 87654321");

        when(jwtGenerator.getUserIdFromToken(AUTHORIZATION_HEADER.substring(7))).thenReturn("1");
        when(accountRepository.findByAccountNumberAndUserId("12345678", 1L)).thenReturn(Optional.of(accountEntity));
        when(accountRepository.findByAccountNumber("87654321")).thenReturn(Optional.of(targetAccount));

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(request, AUTHORIZATION_HEADER);
        });
        assertEquals("Insufficient funds for transfer.", exception.getMessage());
    }

    @Test
    void testCreateTransaction_InvalidTransactionType() {
        final TransactionRequestResource request = TransactionRequestResource.builder()
                .sourceAccountNumber("12345678")
                .transactionType("INVALID")
                .amount(100.0)
                .description("Invalid transaction type")
                .build();
        when(jwtGenerator.getUserIdFromToken(AUTHORIZATION_HEADER.substring(7))).thenReturn("1");
        when(accountRepository.findByAccountNumberAndUserId("12345678", 1L)).thenReturn(Optional.of(new AccountEntity()));

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(request, AUTHORIZATION_HEADER);
        });
        assertEquals("Invalid transaction type: INVALID", exception.getMessage());
    }

    @Test
    void testCreateTransaction_DepositToNonCheckingAccount() {
        final TransactionRequestResource request = TransactionRequestResource.builder()
                .sourceAccountNumber("12345678")
                .transactionType("DEPOSIT")
                .amount(100.0)
                .description("Deposit to non-checking account")
                .build();

        final AccountEntity accountEntity = AccountEntity.builder()
                .accountNumber("12345678")
                .balance(500.0)
                .accountType("SAVINGS")
                .build();

        when(jwtGenerator.getUserIdFromToken(AUTHORIZATION_HEADER.substring(7))).thenReturn("1");
        when(accountRepository.findByAccountNumberAndUserId("12345678", 1L)).thenReturn(Optional.of(accountEntity));

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(request, AUTHORIZATION_HEADER);
        });
        assertEquals("Deposits can only be made to checking accounts.", exception.getMessage());
    }
}
