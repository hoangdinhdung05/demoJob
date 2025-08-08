package com.demoJob.demo.dto.request.User.Client;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserAccountUpdateRequest {
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String firstName;
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String lastName;
}