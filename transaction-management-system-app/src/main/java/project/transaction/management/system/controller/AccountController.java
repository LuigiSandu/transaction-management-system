package project.transaction.management.system.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import project.transaction.management.system.api.resource.account.AccountRequestResource;
import project.transaction.management.system.api.resource.account.AccountResponseResource;
import project.transaction.management.system.api.resource.account.AccountUpdateRequest;
import project.transaction.management.system.service.AccountService;

import java.nio.file.AccessDeniedException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "transaction-management-system/api/v1/accounts")
public class AccountController {

    private final AccountService service;

    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AccountResponseResource> createAccount(@RequestBody @Valid AccountRequestResource request, Authentication authentication) {
        log.debug("Attempting to create account with account number: {}", request.getAccountNumber());

        final String authenticatedUsername = authentication.getName();
        final AccountResponseResource response = service.createAccount(request, authenticatedUsername);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResponseResource> getAccountByNumber(
            @PathVariable @NotBlank String accountNumber, Authentication authentication) throws AccessDeniedException {
        log.debug("Fetching account details for account number: {}", accountNumber);

        final String authenticatedUsername = authentication.getName();
        final AccountResponseResource accountResponse = service.getAccountByNumber(accountNumber, authenticatedUsername);

        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }

    @PatchMapping(value = "/{accountNumber}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResponseResource> updateAccount(
            @PathVariable String accountNumber,
            @RequestBody @Valid AccountUpdateRequest request, Authentication authentication) throws AccessDeniedException {
        log.debug("Updating account details for account number: {}", accountNumber);

        // Pass the authenticated username to the service
        String authenticatedUsername = authentication.getName();
        AccountResponseResource updatedAccount = service.updateAccountName(accountNumber, request, authenticatedUsername);

        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId, Authentication authentication) throws AccessDeniedException {
        log.debug("Attempting to delete account with ID: {}", accountId);

        // Pass the authenticated username to the service
        String authenticatedUsername = authentication.getName();
        service.deleteAccount(accountId, authenticatedUsername);

        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
