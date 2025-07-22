package base_webSocket_demo.dto;

import base_webSocket_demo.dto.response.Admin.User.UserCompanyResponse;
import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles;
}