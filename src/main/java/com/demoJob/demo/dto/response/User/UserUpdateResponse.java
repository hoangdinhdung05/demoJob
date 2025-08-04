package com.demoJob.demo.dto.response.User;

import com.demoJob.demo.util.Gender;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class UserUpdateResponse {

    private String firstName;

    private String lastName;

    private String phone;

    public Gender gender;

    private String avatarUrl;

    private String address;

    private LocalDate birthDate;

    private String website;

}
