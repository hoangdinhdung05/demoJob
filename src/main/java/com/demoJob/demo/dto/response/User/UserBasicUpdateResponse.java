package com.demoJob.demo.dto.response.User;

import com.demoJob.demo.util.Gender;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserBasicUpdateResponse {
    private String firstName;
    private String lastName;
    private String phone;
    private Gender gender;
}