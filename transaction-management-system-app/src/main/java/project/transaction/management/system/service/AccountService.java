package project.transaction.management.system.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.AccountRequestResource;
import project.transaction.management.system.api.resource.AccountResponseResource;
import project.transaction.management.system.dao.repository.AccountRepository;
import project.transaction.management.system.mapper.AccountMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper mapper;
    private final AccountRepository repository;

    public AccountResponseResource createAccount(AccountRequestResource request) {

        return null;
    }

}
