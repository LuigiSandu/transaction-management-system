package project.transaction.management.system.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.transaction.management.system.api.resource.TransactionRequestResource;
import project.transaction.management.system.api.resource.TransactionResponseResource;
import project.transaction.management.system.service.TransactionService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "transaction-management-system/api/v1/transactions")
public class TransactionController {

    private final TransactionService service;

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<TransactionResponseResource> create(@RequestBody @Valid TransactionRequestResource request) {
        log.debug("Creating new Account...");
        final TransactionResponseResource response = service.createTransaction(request);
        log.info("Successfully created Account...");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
