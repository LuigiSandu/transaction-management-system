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

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "transaction-management-system/api/v1/accounts")
public class AccountController {

    private final AccountService service;

    //TODO
    // implement create account
    //TODO retrieve specific account details
    //update account info
    //delete account

    // Create a new account
    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AccountResponseResource> createAccount(@RequestBody @Valid AccountRequestResource request, Authentication authentication) {
        log.debug("Attempting to create account with account number: {}", request.getAccountNumber());

        final AccountResponseResource response = service.createAccount(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResponseResource> getAccountByNumber(
            @PathVariable @NotBlank String accountNumber) {
        log.debug("Fetching account details for account number: {}", accountNumber);

        AccountResponseResource accountResponse = service.getAccountByNumber(accountNumber);

        log.info("Account details retrieved for account number: {}", accountNumber);
        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }

    // Update Account endpoint
    @PatchMapping(value = "/{accountNumber}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResponseResource> updateAccount(
            @PathVariable String accountNumber,
            @RequestBody @Valid AccountUpdateRequest request) {
        log.debug("Updating account details for account number: {}", accountNumber);

        AccountResponseResource updatedAccount = service.updateAccountName(accountNumber, request);

        log.info("Account details updated for account number: {}", accountNumber);
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        log.debug("Attempting to delete account with ID: {}", accountId);

        service.deleteAccount(accountId);

        log.info("Successfully deleted account with ID: {}", accountId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}
