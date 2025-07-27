package base_webSocket_demo.dto.response;

import base_webSocket_demo.util.TokenType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;
}