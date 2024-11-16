package project.transaction.management.system.controller.user;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import project.transaction.management.system.api.resource.user.UserRequestResource;
import project.transaction.management.system.api.resource.user.UserResponseResource;
import project.transaction.management.system.controller.AbstractIntegrationTest;


public abstract class AbstractUserIntegrationTest extends AbstractIntegrationTest {


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
