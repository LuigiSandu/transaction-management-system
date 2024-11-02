package project.transaction.management.system.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.account.AccountRequestResource;
import project.transaction.management.system.api.resource.account.AccountResponseResource;
import project.transaction.management.system.api.resource.account.AccountUpdateRequest;
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

        // Fetch the UserEntity by userId to establish the relationship
        final UserEntity userEntity = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.getUserId()));

        // Map the request to AccountEntity
        final AccountEntity accountEntity = mapper.toEntity(request);

        // Set the UserEntity on the AccountEntity to establish the relationship
        accountEntity.setUser(userEntity); // This line is essential to link the account to the user

        // Save the account entity to the repository
        final AccountResponseResource response = mapper.fromEntity(repository.save(accountEntity));

        // Log the successful creation of the account
        log.info("Successfully created account with account number: {} for user with id: {}", response.getAccountNumber(), userEntity.getId());

        return response;
    }

    public AccountResponseResource getAccountByNumber(String accountNumber) {
        final AccountEntity accountEntity = repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));

        return mapper.fromEntity(accountEntity);
    }

    public AccountResponseResource updateAccountName(String accountNumber, AccountUpdateRequest request) {
        AccountEntity accountEntity = repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));

        accountEntity.setName(request.getName());

        final AccountEntity updatedAccount = repository.save(accountEntity);

        return mapper.fromEntity(updatedAccount);
    }

    public void deleteAccount(Long accountId) {
        log.debug("Deleting account with ID: {}", accountId);

        AccountEntity accountEntity = repository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));

        repository.delete(accountEntity);

        log.info("Account with ID: {} deleted successfully.", accountId);
    }
}
