package project.transaction_management_system;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class TransactionManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionManagementSystemApplication.class, args);
    }

}
