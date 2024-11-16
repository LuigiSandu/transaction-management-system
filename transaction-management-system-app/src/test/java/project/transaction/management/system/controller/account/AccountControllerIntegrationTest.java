package project.transaction.management.system.controller.account;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import project.transaction.management.system.api.resource.account.AccountRequestResource;
import project.transaction.management.system.api.resource.account.AccountResponseResource;

import javax.management.relation.RoleNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountControllerIntegrationTest extends AbstractAccountIntegrationTest {

    @Test
    void testCreateAccount_Success() throws RoleNotFoundException {

        saveUserToDatabase(USERNAME, PASSWORD);

        final String authHeader = loginUser(USERNAME, PASSWORD);

        final AccountRequestResource accountRequest = createAccountRequest(authHeader).getBody();

        final ResponseEntity<AccountResponseResource> response = sendCreateAccountRequest(accountRequest, authHeader);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ACCOUNT_NUMBER, response.getBody().getAccountNumber());
        assertEquals(ACCOUNT_TYPE, response.getBody().getAccountType());
        assertEquals(ACCOUNT_NAME, response.getBody().getName());
        assertEquals(ACCOUNT_BALANCE, response.getBody().getBalance());
    }
}
