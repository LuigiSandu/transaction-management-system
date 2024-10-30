package project.transaction.management.system.api.resource.account;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.transaction.management.system.api.validation.EnumOf;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequestResource {


    @JsonProperty("account_number")
    @NotBlank(message = "Account number is required")
    @Size(min = 8, max = 20, message = "Account number must be between 8 and 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Account number must be alphanumeric")
    private String accountNumber;

    @JsonProperty("account_type")
    @NotNull(message = "Account type is required")
    @EnumOf(value = AccountType.class, message = "Account type must be either 'SAVINGS' or 'CHECKING'")
    private String accountType;

    @JsonProperty("name")
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    @JsonProperty("balance")
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be a positive number")
    private Double balance;

    @JsonProperty("user_id")
    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be a positive number")
    private Long userId;


}
