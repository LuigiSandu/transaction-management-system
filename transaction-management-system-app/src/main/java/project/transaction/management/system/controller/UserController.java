package project.transaction.management.system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.transaction.management.system.api.resource.user.UserLoginRequestResource;
import project.transaction.management.system.api.resource.user.UserRequestResource;
import project.transaction.management.system.api.resource.user.UserResponseResource;
import project.transaction.management.system.api.resource.user.UserUpdateRequestResource;
import project.transaction.management.system.service.UserService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "transaction-management-system/api/v1/users")
public class UserController {

    private final UserService service;

    @PostMapping(value = "/register", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserResponseResource> create(@RequestBody @Valid UserRequestResource request) {
        log.debug("Attempting to create new user with username: {}", request.getUsername());

        final UserResponseResource response = service.createUser(request);

        log.info("Successfully created new User with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserResponseResource> login(@RequestBody @Valid UserLoginRequestResource request) {
        log.debug("Attempting to log in user with username: {}", request.getUsername());

        final UserResponseResource response = service.loginUser(request);

        log.info("User {} logged in successfully.", response.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK); // Use HttpStatus.OK for successful login
    }

    @PatchMapping(value = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserResponseResource> update(
            @RequestBody @Valid UserUpdateRequestResource request
    ) {
        log.debug("Attempting to update user with ID: {}", request.getUserId());

        // Call the service method to update the user details
        final UserResponseResource updatedUser = service.updateUser(request);

        log.info("Successfully updated User with ID: {}", updatedUser.getId());
        return new ResponseEntity<>(updatedUser, HttpStatus.OK); // Return OK for successful update
    }
}


