package project.transaction.management.system.api.resource.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseResource {

    private Long id;

    @JsonProperty("source_account_number")
    private String sourceAccountNumber;

    @JsonProperty("target_account_number")
    private String targetAccountNumber;

    @JsonProperty("transaction_type")
    private String transactionType;

    private Double amount;

    private LocalDateTime timestamp;

    @JsonProperty("description")
    private String description;
}
