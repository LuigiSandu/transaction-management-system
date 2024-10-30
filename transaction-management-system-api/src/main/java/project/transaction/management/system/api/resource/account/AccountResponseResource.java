package project.transaction.management.system.api.resource.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseResource {

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("account_type")
    private String accountType;

    @JsonProperty("name")
    private String name;

    @JsonProperty("balance")
    private Double balance;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt; // Timestamp of when the account was last updated


}
