package project.transaction.management.system.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
        log.debug("Attempting to create account with account number: {} for username: {}", request.getAccountNumber(), authentication.getName());

        final AccountResponseResource response = service.createAccount(request, authentication.getName());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResponseResource> getAccountByNumber(
            @PathVariable @NotBlank String accountNumber, Authentication authentication) throws AccessDeniedException {
        log.debug("Fetching account details for account number: {}", accountNumber);

        final AccountResponseResource accountResponse = service.getAccountByNumber(accountNumber, authentication.getName());

        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }

    @PatchMapping(value = "/{accountNumber}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResponseResource> updateAccount(
            @PathVariable @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Account number must be alphanumeric") String accountNumber,
            @RequestBody @Valid AccountUpdateRequest request, Authentication authentication) throws AccessDeniedException {
        log.debug("Updating account details for account number: {}", accountNumber);

        final AccountResponseResource updatedAccount = service.updateAccountName(accountNumber, request, authentication.getName());

        log.info("Successfully updated account details for account number: {}", accountNumber);
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId, Authentication authentication) throws AccessDeniedException {
        log.debug("Attempting to delete account with ID: {}", accountId);

        service.deleteAccount(accountId, authentication.getName());

        log.info("Account with ID deleted successfully: {}", accountId);
        return ResponseEntity.noContent().build();
    }
}
