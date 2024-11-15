package project.transaction.management.system.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import project.transaction.management.system.api.resource.account.AccountRequestResource;
import project.transaction.management.system.api.resource.account.AccountResponseResource;
import project.transaction.management.system.api.resource.account.AccountUpdateRequest;
import project.transaction.management.system.dao.entity.AccountEntity;
import project.transaction.management.system.dao.entity.UserEntity;
import project.transaction.management.system.dao.repository.AccountRepository;
import project.transaction.management.system.dao.repository.UserRepository;
import project.transaction.management.system.exception.AccountNotFoundException;
import project.transaction.management.system.mapper.AccountMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static project.transaction.management.system.AbstractTest.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountMapper mapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("Create Account Successfully")
    void createAccount_Success() {
        final AccountRequestResource request = createAccountRequestResource();
        final UserEntity userEntity = createUserEntityResource();
        final AccountEntity accountEntity = createAccountEntityResource();
        final AccountResponseResource expectedResponse = createAccountResponseResource();

        when(userRepository.findByUsername(AUTHENTICATED_USERNAME)).thenReturn(Optional.of(userEntity));
        when(accountRepository.existsByAccountNumber(accountEntity.getAccountNumber())).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(accountEntity);
        when(accountRepository.save(accountEntity)).thenReturn(accountEntity);
        when(mapper.fromEntity(accountEntity)).thenReturn(expectedResponse);

        final AccountResponseResource actualResponse = accountService.createAccount(request, AUTHENTICATED_USERNAME);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getAccountNumber(), actualResponse.getAccountNumber());
        assertEquals(expectedResponse.getAccountType(), actualResponse.getAccountType());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getBalance(), actualResponse.getBalance());
        assertEquals(expectedResponse.getUserId(), actualResponse.getUserId());
        verify(accountRepository, times(1)).save(accountEntity);
    }

    @Test
    @DisplayName("Create Account - Account Number Already Exists")
    void createAccount_AccountNumberExists() {
        final String authenticatedUsername = "username1";
        final AccountRequestResource request = createAccountRequestResource();
        final AccountEntity accountEntity = createAccountEntityResource();

        when(userRepository.findByUsername(authenticatedUsername)).thenReturn(Optional.of(createUserEntityResource()));
        when(accountRepository.existsByAccountNumber(accountEntity.getAccountNumber())).thenReturn(true);
        when(mapper.toEntity(request)).thenReturn(accountEntity);

        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(request, authenticatedUsername));
    }

    @Test
    @DisplayName("Get Account by Number Successfully")
    void getAccountByNumber_Success() {
        final AccountEntity accountEntity = createAccountEntityResource();
        accountEntity.setAccountNumber(ACCOUNT_NUMBER);

        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(accountEntity));
        when(mapper.fromEntity(accountEntity)).thenReturn(createAccountResponseResource());

        final AccountResponseResource actualResponse = accountService.getAccountByNumber(ACCOUNT_NUMBER, AUTHENTICATED_USERNAME);

        assertNotNull(actualResponse);
        assertEquals(accountEntity.getAccountNumber(), actualResponse.getAccountNumber());
        verify(accountRepository, times(1)).findByAccountNumber(ACCOUNT_NUMBER);
    }

    @Test
    @DisplayName("Get Account by Number - Account Not Found")
    void getAccountByNumber_AccountNotFound() {

        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountByNumber(ACCOUNT_NUMBER, AUTHENTICATED_USERNAME));
    }

    @Test
    @DisplayName("Update Account Name Successfully")
    void updateAccountName_Success() {
        final AccountUpdateRequest updateRequest = createAccountUpdateRequestResource();
        final AccountEntity accountEntity = createAccountEntityResource();
        accountEntity.setAccountNumber(ACCOUNT_NUMBER);
        final AccountEntity updatedAccount = createAccountEntityUpdated();

        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(accountEntity));
        when(mapper.fromEntity(updatedAccount)).thenReturn(createAccountResponseResourceUpdated());
        when(accountRepository.saveAndFlush(accountEntity)).thenReturn(updatedAccount);

        final AccountResponseResource actualResponse = accountService.updateAccountName(ACCOUNT_NUMBER, updateRequest, AUTHENTICATED_USERNAME);

        assertNotNull(actualResponse);
        assertEquals(updatedAccount.getName(), actualResponse.getName());
        verify(accountRepository, times(1)).saveAndFlush(accountEntity);
    }


    @Test
    @DisplayName("Update Account Name - Account Not Found")
    void updateAccountName_AccountNotFound() {
        final AccountUpdateRequest updateRequest = createAccountUpdateRequestResource();

        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.updateAccountName(ACCOUNT_NUMBER, updateRequest, AUTHENTICATED_USERNAME));
    }

    @Test
    @DisplayName("Delete Account Successfully")
    void deleteAccount_Success() {
        final Long accountId = 1L;
        final AccountEntity accountEntity = createAccountEntityResource();
        accountEntity.setId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountEntity));
        doNothing().when(accountRepository).delete(accountEntity);

        accountService.deleteAccount(accountId, AUTHENTICATED_USERNAME);

        verify(accountRepository, times(1)).delete(accountEntity);
    }

    @Test
    @DisplayName("Delete Account - Account Not Found")
    void deleteAccount_AccountNotFound() {
        final Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.deleteAccount(accountId, AUTHENTICATED_USERNAME));
    }

    @Test
    @DisplayName("Access Denied - Unauthorized User")
    void accessDenied_UnauthorizedUser() {
        final String accountNumber = "12345";
        final String authenticatedUsername = "unauthorizedUser";
        final AccountEntity accountEntity = createAccountEntityResource();
        accountEntity.setAccountNumber(accountNumber);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(accountEntity));

        assertThrows(AccessDeniedException.class, () -> accountService.getAccountByNumber(accountNumber, authenticatedUsername));
    }
}
