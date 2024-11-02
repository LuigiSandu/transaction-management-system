package project.transaction.management.system.api.resource.transaction;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("transaction_type")
    @NotNull(message = "Transaction type is required")
    @EnumOf(value = TransactionType.class, message = "Transaction type must be either 'TRANSFER', 'DEPOSIT', or 'WITHDRAWAL'")
    private String transactionType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be a positive number")
    private Double amount;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}
