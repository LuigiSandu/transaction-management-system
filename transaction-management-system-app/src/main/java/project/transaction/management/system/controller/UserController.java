package project.transaction.management.system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.transaction.management.system.api.resource.transaction.TransactionResponseResource;
import project.transaction.management.system.api.resource.user.*;
import project.transaction.management.system.service.NotificationService;
import project.transaction.management.system.service.TransactionService;
import project.transaction.management.system.service.UserService;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "transaction-management-system/api/v1/users")
public class UserController {

    private final UserService service;
    private final TransactionService transactionService;

    @PostMapping(value = "/register", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserResponseResource> create(@RequestBody @Valid UserRequestResource request) throws RoleNotFoundException {
        log.debug("Attempting to create new user with username: {}", request.getUsername());

        final UserResponseResource response = service.createUser(request);

        log.info("Successfully created new User with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserTokenResponse> login(@RequestBody @Valid UserLoginRequestResource request) {
        log.debug("Attempting to log in user with username: {}", request.getUsername());

        final String response = service.loginUser(request);

        log.info("User {} logged in successfully.", request.getUsername());
        return new ResponseEntity<>(new UserTokenResponse(response), HttpStatus.OK); // Use HttpStatus.OK for successful login
    }

    @PatchMapping(value = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserResponseResource> update(@RequestHeader("Authorization") String authorizationHeader,
                                                       @RequestBody @Valid UserUpdateRequestResource request) {

        final UserResponseResource updatedUser = service.updateUser(request, authorizationHeader);

        log.info("Successfully updated User with ID: {}", updatedUser.getId());
        return new ResponseEntity<>(updatedUser, HttpStatus.OK); // Return OK for successful update
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<TransactionResponseResource>> getAllTransactions(@RequestHeader("Authorization") String authorizationHeader) {
        final List<TransactionResponseResource> transactions = transactionService.getAllTransactionsByUserId(authorizationHeader);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}


