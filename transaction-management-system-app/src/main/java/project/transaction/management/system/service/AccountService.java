package project.transaction.management.system.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.account.AccountRequestResource;
import project.transaction.management.system.api.resource.account.AccountResponseResource;
import project.transaction.management.system.dao.entity.AccountEntity;
import project.transaction.management.system.dao.entity.UserEntity;
import project.transaction.management.system.dao.repository.AccountRepository;
import project.transaction.management.system.dao.repository.UserRepository;
import project.transaction.management.system.mapper.AccountMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper mapper;
    private final AccountRepository repository;
    private final UserRepository userRepository;

    public AccountResponseResource createAccount(AccountRequestResource request) {

        // Fetch UserEntity by userId to set the relationship
        final UserEntity userEntity = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.getUserId()));

        // Map request to AccountEntity and set the UserEntity
        final AccountEntity accountEntity = mapper.toEntity(request);
        accountEntity.setUser(userEntity); // Set the user in the account entity

        // Save and map to response
        final AccountResponseResource response = mapper.fromEntity(repository.save(accountEntity));

        log.info("Successfully created account with account number: {} for user with id: {}", response.getAccountNumber(), userEntity.getId());
        return response;
    }

    public AccountResponseResource getAccountByNumber(String accountNumber) {
        final AccountEntity accountEntity = repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));

        return mapper.fromEntity(accountEntity);
    }

    public AccountResponseResource updateAccount(String accountNumber, AccountRequestResource request) {
        AccountEntity accountEntity = repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));

        // Update account fields with the values from the request
        if (request.getAccountType() != null) {
            accountEntity.setAccountType(request.getAccountType());
        }
        if (request.getName() != null) {
            accountEntity.setName(request.getName());
        }
        if (request.getBalance() != null) {
            accountEntity.setBalance(request.getBalance());
        }

        // Save the updated account entity
        AccountEntity updatedAccount = repository.save(accountEntity);

        return mapper.fromEntity(updatedAccount);
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        log.debug("Deleting account with ID: {}", accountId);

        // Check if the account exists
        AccountEntity accountEntity = repository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));

        // Optionally handle any associated transactions here
        // For example, if you want to delete transactions related to the account:
        // transactionRepository.deleteByAccountId(accountId);

        // Delete the account
        repository.delete(accountEntity);

        log.info("Account with ID: {} deleted successfully.", accountId);
    }
}
