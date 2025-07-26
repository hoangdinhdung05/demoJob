package base_webSocket_demo.dto.response;

import base_webSocket_demo.util.OtpType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyOtpRequest {
    private String email;
    private String code;
    private OtpType type;
}

