package project.transaction.management.system.api.resource.user;

import lombok.Data;

@Data
public class UserTokenResponse {

    private String accessToken;
    private String tokenType = "Bearer: ";

    public UserTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
