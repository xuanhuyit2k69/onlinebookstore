package vn.edu.xyz.olms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private String username;
    private String role;
    private UUID memberId;

    @JsonProperty("token")
    public String getTokenAlias() {
        return accessToken;
    }
}
