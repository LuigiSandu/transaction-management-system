package project.transaction.management.system.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.transaction.management.system.api.resource.transaction.TransactionRequestResource;
import project.transaction.management.system.api.resource.transaction.TransactionResponseResource;
import project.transaction.management.system.service.TransactionService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "transaction-management-system/api/v1/transactions")
public class TransactionController {

    private final TransactionService service;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseResource> createTransaction(@RequestBody @Valid TransactionRequestResource request) {
        log.debug("Attempting to create transaction for account number: {}", request.getSourceAccountNumber());

        // Get the authenticated user's ID
       final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal(); // Assuming the principal is the user ID

        // Call the service to create the transaction
        final TransactionResponseResource response = service.createTransaction(request, userId);

        log.info("Successfully created transaction with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED); // Return HTTP 201 Created
    }

}
