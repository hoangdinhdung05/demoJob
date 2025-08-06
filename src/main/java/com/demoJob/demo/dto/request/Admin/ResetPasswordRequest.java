package com.demoJob.demo.dto.request.Admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Verify key không được để trống")
    private String verifyKey;
    @NotBlank(message = "Password mới không được để trống")
    private String newPassword;
    @NotBlank(message = "Xác nhận password không được để trống")
    private String confirmPassword;
}

