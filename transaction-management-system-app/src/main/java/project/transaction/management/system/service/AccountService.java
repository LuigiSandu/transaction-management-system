package project.transaction.management.system.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.account.AccountRequestResource;
import project.transaction.management.system.api.resource.account.AccountResponseResource;
import project.transaction.management.system.api.resource.account.AccountUpdateRequest;
import project.transaction.management.system.dao.entity.AccountEntity;
import project.transaction.management.system.dao.entity.UserEntity;
import project.transaction.management.system.dao.repository.AccountRepository;
import project.transaction.management.system.dao.repository.UserRepository;
import project.transaction.management.system.mapper.AccountMapper;

import java.nio.file.AccessDeniedException;

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

        // Map the request to AccountEntity
        final AccountEntity accountEntity = mapper.toEntity(request);

        // Check for account number uniqueness
        if (repository.existsByAccountNumber(accountEntity.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists: " + accountEntity.getAccountNumber());
        }

        // Set the UserEntity on the AccountEntity to establish the relationship
        accountEntity.setUser(userEntity); // Link the account to the authenticated user only

        // Save the account entity to the repository
        final AccountResponseResource response = mapper.fromEntity(repository.save(accountEntity));

        // Log the successful creation of the account
        log.info("Successfully created account with account number: {} for user with username: {}", response.getAccountNumber(), authenticatedUsername);

        return response;
    }

    public AccountResponseResource getAccountByNumber(String accountNumber, String authenticatedUsername) throws AccessDeniedException {
        AccountEntity account = repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));

        // Ensure the account belongs to the authenticated user
        if (!account.getUser().getUsername().equals(authenticatedUsername)) {
            throw new AccessDeniedException("User not authorized to access this account");
        }

        return mapper.fromEntity(account);
    }

    public AccountResponseResource updateAccountName(String accountNumber, AccountUpdateRequest request, String authenticatedUsername) throws AccessDeniedException {
        AccountEntity account = repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));

        if (!account.getUser().getUsername().equals(authenticatedUsername)) {
            throw new AccessDeniedException("User not authorized to update this account");
        }

        account.setName(request.getName());
        AccountEntity updatedAccount = repository.save(account);
        return mapper.fromEntity(updatedAccount);
    }

    public void deleteAccount(Long accountId, String authenticatedUsername) throws AccessDeniedException {
        AccountEntity account = repository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));

        if (!account.getUser().getUsername().equals(authenticatedUsername)) {
            throw new AccessDeniedException("User not authorized to delete this account");
        }

        repository.delete(account);
    }
}
