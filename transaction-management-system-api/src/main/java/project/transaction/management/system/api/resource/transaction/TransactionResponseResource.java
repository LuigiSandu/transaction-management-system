package project.transaction.management.system.api.resource.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseResource {

    private Long id; // ID of the transaction

    @JsonProperty("source_account_number")
    private String sourceAccountNumber;

    @JsonProperty("target_account_number")
    private String targetAccountNumber;

    @JsonProperty("transaction_type")
    private String transactionType; // The type of transaction

    private Double amount; // The amount of the transaction

    private LocalDateTime timestamp; // The timestamp of the transaction

    @JsonProperty("description")
    private String description; // Description of the transaction
}
