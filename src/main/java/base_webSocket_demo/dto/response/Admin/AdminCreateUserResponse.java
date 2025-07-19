package base_webSocket_demo.dto.response.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AdminCreateUserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;

    private AdminUserProfileResponse userProfile;

    private Set<RoleResponse> roles;
}
