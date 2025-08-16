package com.demoJob.demo.dto.request.User.Client;

import com.demoJob.demo.util.Gender;
import com.demoJob.demo.util.validator.PhoneNumber;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class UserProfileUpdateRequest {
    private String avatarUrl;
    private String address;
    @PhoneNumber
    private String phone;
    private LocalDate birthDate;
    private Gender gender;
    private String website;
}
