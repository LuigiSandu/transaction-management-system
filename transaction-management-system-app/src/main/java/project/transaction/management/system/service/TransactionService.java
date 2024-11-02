package project.transaction.management.system.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.transaction.management.system.api.resource.transaction.TransactionRequestResource;
import project.transaction.management.system.api.resource.transaction.TransactionResponseResource;
import project.transaction.management.system.dao.entity.TransactionEntity;
import project.transaction.management.system.dao.repository.TransactionReposittory;
import project.transaction.management.system.mapper.TransactionMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionMapper mapper;
    private final TransactionReposittory repository;

    public TransactionResponseResource createTransaction(TransactionRequestResource request) {
        // Create a new TransactionEntity from the request
        TransactionEntity transactionEntity = mapper.toEntity(request);


        // Save the transaction to the repository
        TransactionEntity savedTransaction = repository.save(transactionEntity);

        // Map the saved entity to a response resource and return it
        return mapper.fromEntity(savedTransaction);
    }
}


