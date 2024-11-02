package project.transaction.management.system.api.resource.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountUpdateRequest {


    @JsonProperty("name")
    @Size(min = 5, max = 50, message = "Name must be between 5 and 50 characters")
    @NotBlank(message = "Account Name is required")
    private String name;

}
