package base_webSocket_demo.dto.request.Admin.Users;

import base_webSocket_demo.util.Gender;
import base_webSocket_demo.util.validator.PhoneNumber;
import lombok.Getter;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
public class UserProfileRequest implements Serializable {

    private String avatarUrl;
    private String address;

    @PhoneNumber
    private String phone;

    private LocalDate birthDate;
    private Gender gender;

}
