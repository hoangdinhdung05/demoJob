package base_webSocket_demo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
//    private String role;
}