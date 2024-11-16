package project.transaction.management.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import project.transaction.management.system.TransactionManagementSystemApplication;
import project.transaction.management.system.service.UserService;

@SpringBootTest(classes = TransactionManagementSystemApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration-test.yml")
@ActiveProfiles("integration-test")
public class AbstractIntegrationTest {

    @Autowired
    protected TestRestTemplate restTemplate;
    @Autowired
    protected UserService userService;

    private static final String BASE_PATH = "/transaction-management-system/api/v1";

    protected String getBaseUrl() {
        return restTemplate.getRootUri() + BASE_PATH;
    }
}
