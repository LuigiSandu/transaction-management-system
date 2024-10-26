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

    @PutMapping(value = "/{userId}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserResponseResource> login(@RequestBody @Valid UserRequestResource request,
                                                      @PathVariable("userId") String userId) {
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

}
