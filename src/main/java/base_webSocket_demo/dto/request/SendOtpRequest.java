package base_webSocket_demo.dto.request;

import base_webSocket_demo.util.OtpType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendOtpRequest {
    private String email;
    private OtpType type;
}

