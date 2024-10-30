package project.transaction.management.system.api.resource;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
