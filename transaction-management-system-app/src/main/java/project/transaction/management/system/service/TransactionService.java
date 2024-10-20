package project.transaction.management.system.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.TransactionRequestResource;
import project.transaction.management.system.api.resource.TransactionResponseResource;
import project.transaction.management.system.dao.repository.TransactionReposittory;
import project.transaction.management.system.mapper.TransactionMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionMapper mapper;
    private final TransactionReposittory repository;

    public TransactionResponseResource createTransaction(TransactionRequestResource request) {

        return null;
    }

}
