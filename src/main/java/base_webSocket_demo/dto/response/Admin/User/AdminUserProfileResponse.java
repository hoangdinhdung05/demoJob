package base_webSocket_demo.dto.response.Admin.User;

import base_webSocket_demo.util.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AdminUserProfileResponse {
    private String avatarUrl;
    private String address;
    private String phone;
    private LocalDate birthDate;
    private Gender gender;
}
