package project.transaction.management.system.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.account.AccountRequestResource;
import project.transaction.management.system.api.resource.account.AccountResponseResource;
import project.transaction.management.system.api.resource.account.AccountUpdateRequest;
import project.transaction.management.system.dao.entity.AccountEntity;
import project.transaction.management.system.dao.entity.UserEntity;
import project.transaction.management.system.dao.repository.AccountRepository;
import project.transaction.management.system.dao.repository.UserRepository;
import project.transaction.management.system.exception.AccountNotFoundException;
import project.transaction.management.system.mapper.AccountMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper mapper;
    private final AccountRepository repository;
    private final UserRepository userRepository;

    @Transactional
    public AccountResponseResource createAccount(AccountRequestResource request, String authenticatedUsername) {
        final UserEntity userEntity = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + authenticatedUsername));

        final AccountEntity accountEntity = mapper.toEntity(request);
        if (repository.existsByAccountNumber(accountEntity.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists: " + accountEntity.getAccountNumber());
        }

        accountEntity.setUser(userEntity);
        final AccountResponseResource response = mapper.fromEntity(repository.save(accountEntity));

        log.info("Successfully created account with account number: {} for user: {}", response.getAccountNumber(), authenticatedUsername);
        return response;
    }

    public AccountResponseResource getAccountByNumber(String accountNumber, String authenticatedUsername) {
        final AccountEntity accountEntity = repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with account number: " + accountNumber));

        authorizeUserAccess(accountEntity, authenticatedUsername, "retrieve");
        return mapper.fromEntity(accountEntity);
    }

    @Transactional
    public AccountResponseResource updateAccountName(String accountNumber, AccountUpdateRequest request, String authenticatedUsername) {
        final AccountEntity accountEntity = repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with account number: " + accountNumber));

        authorizeUserAccess(accountEntity, authenticatedUsername, "update");

        accountEntity.setName(request.getName());
        final AccountEntity updatedAccount = repository.saveAndFlush(accountEntity);
        log.info("Updated account name for account number: {} by user: {}", accountNumber, authenticatedUsername);
        return mapper.fromEntity(updatedAccount);
    }

    @Transactional
    public void deleteAccount(Long accountId, String authenticatedUsername) {
        final AccountEntity accountEntity = repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        authorizeUserAccess(accountEntity, authenticatedUsername, "delete");

        repository.delete(accountEntity);
        log.info("Deleted account with ID: {} by user: {}", accountId, authenticatedUsername);
    }

    private void authorizeUserAccess(AccountEntity account, String authenticatedUsername, String action) {
        if (!account.getUser().getUsername().equals(authenticatedUsername)) {
            throw new AccessDeniedException("User not authorized to " + action + " this account");
        }
    }

}
