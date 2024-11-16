package project.transaction.management.system.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import project.transaction.management.system.AbstractIntegrationTest;
import project.transaction.management.system.api.resource.user.UserResponseResource;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerPostIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testCreateUser() {
        ResponseEntity<UserResponseResource> registerResponse = createUser("username2", "Password@2", "email2@example.com");

        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());
        assertEquals("username2", registerResponse.getBody().getUsername());
        assertEquals("email2@example.com", registerResponse.getBody().getEmail());
    }

    @Test
    void testAuthenticateUser() {
        // Act: Create a new user first
        createUser("username1", "Password@1", "username1@example.com");

        ResponseEntity<String> loginResponse = authenticateUser("username1", "Password@1");

        // Assert: Check if login is successful and that a valid token is returned
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody()); // Assuming JWT token or session ID is returned
        assertTrue(loginResponse.getBody().length() > 0); // Ensure a token is returned (JWT or other)
    }
}
