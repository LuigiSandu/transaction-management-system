package project.transaction.management.system.controller.account;

import org.springframework.http.*;
import project.transaction.management.system.api.resource.account.AccountRequestResource;
import project.transaction.management.system.api.resource.account.AccountResponseResource;
import project.transaction.management.system.api.resource.account.AccountType;
import project.transaction.management.system.api.resource.user.UserLoginRequestResource;
import project.transaction.management.system.api.resource.user.UserRequestResource;
import project.transaction.management.system.api.resource.user.UserTokenResponse;
import project.transaction.management.system.controller.AbstractIntegrationTest;

import javax.management.relation.RoleNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractAccountIntegrationTest extends AbstractIntegrationTest {

    public static final String USERNAME = "username21";
    public static final String PASSWORD = "Password@1";
    protected static final String ACCOUNT_NUMBER = "1234567890";
    protected static final String ACCOUNT_TYPE = AccountType.CHECKING.name();
    protected static final String ACCOUNT_NAME = "Test Account";
    protected static final double ACCOUNT_BALANCE = 1000.00;

    protected void saveUserToDatabase(String username, String password) throws RoleNotFoundException {
        UserRequestResource userRequest = UserRequestResource.builder()
                .username(username)
                .password(password)
                .email("email21@gmail.com")
                .build();

        userService.createUser(userRequest);
    }

    protected String loginUser(String username, String password) {
        final UserLoginRequestResource loginRequest = UserLoginRequestResource.builder()
                .username(username)
                .password(password)
                .build();

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<UserLoginRequestResource> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<UserTokenResponse> response = restTemplate.postForEntity(
                getBaseUrl() + "/users/login",  // Login endpoint
                request,
                UserTokenResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        return "Bearer " + response.getBody().getAccessToken();
    }

    protected HttpEntity<AccountRequestResource> createAccountRequest(String authHeader) {
        final AccountRequestResource accountRequest = AccountRequestResource.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountType(ACCOUNT_TYPE)
                .name(ACCOUNT_NAME)
                .balance(ACCOUNT_BALANCE)
                .build();

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authHeader);
        headers.add("Content-Type", "application/json");

        return new HttpEntity<>(accountRequest, headers);
    }

    protected ResponseEntity<AccountResponseResource> sendCreateAccountRequest(
            AccountRequestResource accountRequest, String authHeader) {

        HttpEntity<AccountRequestResource> request = new HttpEntity<>(accountRequest, createAuthHeaders(authHeader));
        return restTemplate.exchange(getBaseUrl() + "/accounts/create", HttpMethod.POST, request, AccountResponseResource.class);
    }

    // Helper method to create authorization headers
    private HttpHeaders createAuthHeaders(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authHeader);
        headers.add("Content-Type", "application/json");
        return headers;
    }
}
