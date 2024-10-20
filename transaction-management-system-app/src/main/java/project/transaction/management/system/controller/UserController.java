package project.transaction.management.system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.transaction.management.system.api.resource.UserRequestResource;
import project.transaction.management.system.api.resource.UserResponseResource;
import project.transaction.management.system.service.UserService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "transaction-management-system/api/v1/users")
public class UserController {

    private final UserService service;

    @PostMapping(value = "/register", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<UserResponseResource> create(@RequestBody @Valid UserRequestResource request) {
        log.debug("Creating new User...");
        final UserResponseResource response = service.createUser(request);
        log.info("Successfully created new User...");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/login", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<UserResponseResource> login(@RequestBody @Valid UserRequestResource request) {
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{userId}}", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<UserResponseResource> login(@RequestBody @Valid UserRequestResource request,
            @PathVariable("userId") String userId) {
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

}
