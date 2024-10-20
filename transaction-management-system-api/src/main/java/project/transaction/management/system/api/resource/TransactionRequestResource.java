package project.transaction.management.system.api.resource;


import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestResource {

    private String accountId;
    private String transactionType;
    private Long amount;
    private LocalDate timestamp;
    private String description;
}