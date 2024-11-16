package project.transaction.management.system.api.resource.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTokenResponse {

    private String accessToken;
    private String tokenType = "Bearer: ";

    public UserTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
