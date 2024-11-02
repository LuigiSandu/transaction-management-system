package project.transaction.management.system.api.resource.transaction;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseResource {

    private Long id; // ID of the transaction

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("transaction_type")
    private String transactionType; // The type of transaction

    private Double amount; // The amount of the transaction

    private LocalDate timestamp; // The timestamp of the transaction

    @JsonProperty("description")
    private String description; // Description of the transaction
}
