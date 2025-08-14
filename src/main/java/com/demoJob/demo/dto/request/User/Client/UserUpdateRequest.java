package com.demoJob.demo.dto.request.User.Client;

import com.demoJob.demo.util.Gender;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserUpdateRequest {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    private String phone;

    public Gender gender;

    private String avatarUrl;

    private String address;

    private LocalDate birthDate;

    private String website;
}
