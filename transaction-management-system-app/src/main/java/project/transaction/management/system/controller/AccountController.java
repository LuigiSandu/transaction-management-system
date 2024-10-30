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
import project.transaction.management.system.api.resource.account.AccountRequestResource;
import project.transaction.management.system.api.resource.account.AccountResponseResource;
import project.transaction.management.system.service.AccountService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "transaction-management-system/api/v1/accounts")
public class AccountController {

    private final AccountService service;

    //TODO
    // implement create account
    //retrieve specific account details
    //update account info
    //delete account

    // Create a new account
    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AccountResponseResource> createAccount(@RequestBody @Valid AccountRequestResource request) {
        log.debug("Attempting to create account with account number: {}", request.getAccountNumber());

        // Call the service to create an account
        final AccountResponseResource response = service.createAccount(request);


        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
