package project.transaction.management.system.service;


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

        log.info("Successfully created account with account number: {} for user with id: {}", response.getAccountNumber(),userEntity.getId());
        return response;
    }

}
