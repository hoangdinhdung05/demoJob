package base_webSocket_demo.dto.response.Admin.User;

import base_webSocket_demo.entity.UserCompany;
import base_webSocket_demo.util.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    private UserStatus userStatus;
    private AdminUserProfileResponse userProfile;
    private Set<RoleUseResponse> roles;
    private UserCompanyResponse companyInfo;
}
