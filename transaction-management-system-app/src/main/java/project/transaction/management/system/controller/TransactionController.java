package project.transaction.management.system.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import project.transaction.management.system.api.resource.transaction.TransactionRequestResource;
import project.transaction.management.system.api.resource.transaction.TransactionResponseResource;
import project.transaction.management.system.service.NotificationService;
import project.transaction.management.system.service.TransactionService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "transaction-management-system/api/v1/transactions")
public class TransactionController {

    private final TransactionService service;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseResource> createTransaction(@RequestHeader("Authorization") String authorizationHeader, @RequestBody @Valid TransactionRequestResource request, Authentication authentication) {
        log.debug("Attempting to create transaction for account number: {}", request.getSourceAccountNumber());

        final TransactionResponseResource response = service.createTransaction(request, authorizationHeader);
            NotificationService.completeTransaction(response);
                    log.info("Successfully created transaction with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED); // Return HTTP 201 Created
    }

}
