package base_webSocket_demo.dto.request.Admin.Users;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.io.Serializable;
import java.util.Set;

@Getter
@AllArgsConstructor
public class UserRequest implements Serializable {

    @NotBlank(message = "firstName must be not blank")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "lastName must be not blank")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email invalid format")
    private String email;

    @NotBlank(message = "Username must be not blank")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @Size(min = 6, message = "Password must be greater than or equal to 6 characters in length")
    @NotBlank(message = "Password must not be blank")
    private String password;

    private UserProfileRequest userProfile;

    @NotEmpty(message = "User must have at least one role")
    private Set<String> roles;

}
