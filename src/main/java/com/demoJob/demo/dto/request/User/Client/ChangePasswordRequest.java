package com.demoJob.demo.dto.request.User.Client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ChangePasswordRequest {
    @NotBlank
    @Size(min = 6, max = 20, message = "Password must be between 8 and 20 characters")
    private String currentPassword;

    @NotBlank
    @Size(min = 6, max = 20, message = "New password must be between 8 and 20 characters")
    private String newPassword;
}
