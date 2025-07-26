package base_webSocket_demo.dto.request;

import lombok.Getter;

@Getter
public class LoginEmailRequest {
    private String email;
    private String password;
}
