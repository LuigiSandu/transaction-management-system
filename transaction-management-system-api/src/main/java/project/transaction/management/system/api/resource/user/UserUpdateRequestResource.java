package project.transaction.management.system.api.resource.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestResource {


    @JsonProperty("username")
    @Size(min = 6, max = 20, message = "Username should have between 6 and 20 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain alphanumeric characters and underscores.")
    private String username;

    @JsonProperty("password")
    @Size(min = 6, max = 20, message = "Password should have between 6 and 20 characters.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,20}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
    private String password;

    @JsonProperty("email")
    @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email address.")
    private String email;
}
