package base_webSocket_demo.dto.request.Admin.Users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import java.util.Set;

@Getter
public class UserUpdateRequest {
    private String firstName;
    private String lastName;

    @Email
    private String email;

    private String username;

    @Size(min = 6)
    private String password;

    private UserProfileRequest userProfile;

    private UserCompanyRequest companyInfo;

    private Set<String> roles;
}