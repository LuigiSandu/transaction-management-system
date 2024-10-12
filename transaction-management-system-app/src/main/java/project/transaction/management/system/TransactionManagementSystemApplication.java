package project.transaction.management.system;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class },
scanBasePackages = {"project.transaction.management.system"})
public class TransactionManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionManagementSystemApplication.class, args);
    }

}
