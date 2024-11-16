package project.transaction.management.system.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import project.transaction.management.system.api.resource.user.UserResponseResource;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerIntegrationTest extends AbstractUserIntegrationTest {

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
        createUser("username1", "Password@1", "username1@example.com");

        ResponseEntity<String> loginResponse = authenticateUser("username1", "Password@1");

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody()); // Assuming JWT token or session ID is returned
        assertTrue(loginResponse.getBody().length() > 0); // Ensure a token is returned (JWT or other)
    }
}
