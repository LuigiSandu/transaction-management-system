package project.transaction.management.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import project.transaction.management.system.api.resource.user.UserRequestResource;
import project.transaction.management.system.api.resource.user.UserResponseResource;

@SpringBootTest(classes = TransactionManagementSystemApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration-test.yml")
@ActiveProfiles("integration-test")
public abstract class AbstractIntegrationTest {

    private static final String BASE_PATH = "/transaction-management-system/api/v1";

    @Autowired
    protected TestRestTemplate restTemplate;

    protected String getBaseUrl() {
        return restTemplate.getRootUri() + BASE_PATH;
    }

    // Utility method to create a user and return the response
    protected ResponseEntity<UserResponseResource> createUser(String username, String password, String email) {
        UserRequestResource userRequest = UserRequestResource.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<UserRequestResource> request = new HttpEntity<>(userRequest, headers);

        return restTemplate.postForEntity(getBaseUrl() + "/users/register", request, UserResponseResource.class);
    }

    // Utility method to authenticate a user and return the login response
    protected ResponseEntity<String> authenticateUser(String username, String password) {
        UserRequestResource authRequest = UserRequestResource.builder()
                .username(username)
                .password(password)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<UserRequestResource> authRequestEntity = new HttpEntity<>(authRequest, headers);

        return restTemplate.postForEntity(getBaseUrl() + "/users/login", authRequestEntity, String.class);
    }
}
