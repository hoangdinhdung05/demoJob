package com.demoJob.demo.dto.request.User.Client;

import com.demoJob.demo.util.enums.Gender;
import com.demoJob.demo.util.validator.PhoneNumber;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class UserProfileRequest {

    private String avatarUrl;
    private String address;
    @PhoneNumber
    private String phone;
    private LocalDate birthDate;
    private Gender gender;

}
