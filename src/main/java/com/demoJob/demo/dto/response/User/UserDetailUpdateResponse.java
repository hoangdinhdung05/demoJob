package com.demoJob.demo.dto.response.User;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class UserDetailUpdateResponse {
    private String avatarUrl;
    private String address;
    private LocalDate birthDate;
    private String website;
}