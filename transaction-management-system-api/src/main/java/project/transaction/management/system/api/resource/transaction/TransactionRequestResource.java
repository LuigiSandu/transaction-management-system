package project.transaction.management.system.api.resource.transaction;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.transaction.management.system.api.validation.EnumOf;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestResource {

    @NotBlank(message = "Account Number is required")
    @JsonProperty("source_account_number")
    @Size(min = 8, max = 20, message = "Account number must be between 8 and 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Account number must be alphanumeric")
    private String sourceAccountNumber;

    @JsonProperty("transaction_type")
    @NotNull(message = "Transaction type is required")
    @EnumOf(value = TransactionType.class, message = "Transaction type must be either 'TRANSFER', 'DEPOSIT', or 'WITHDRAWAL'")
    private String transactionType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be a positive number")
    private Double amount;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @JsonProperty("target_account_number")
    private String targetAccountNumber;

}
