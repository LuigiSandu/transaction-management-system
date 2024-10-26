package project.transaction.management.system.api.resource.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequestResource {

    @JsonProperty("username")
    @NotBlank(message = "Username is required.") // Ensure username is not empty
    @Size(min = 6, max = 20, message = "Username should have between 6 and 20 characters.")
    private String username;

    @JsonProperty("password")
    @NotBlank(message = "Password is required.") // Ensure password is not empty
    @Size(min = 6, max = 20, message = "Password should have between 6 and 20 characters.")
    private String password;


}