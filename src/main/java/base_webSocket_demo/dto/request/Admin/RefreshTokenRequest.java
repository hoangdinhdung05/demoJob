package base_webSocket_demo.dto.request.Admin;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}