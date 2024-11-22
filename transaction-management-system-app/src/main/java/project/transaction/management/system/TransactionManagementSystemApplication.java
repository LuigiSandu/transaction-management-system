package project.transaction.management.system;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = {"project.transaction.management.system",
                "project.transaction.management.system.dao"})
@EnableJpaRepositories(basePackages = "project.transaction.management.system.dao")
public class TransactionManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionManagementSystemApplication.class, args);
    }

}
