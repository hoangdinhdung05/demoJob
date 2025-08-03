package com.demoJob.demo.dto.request.Admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @Size(min = 6, max = 20, message = "Mật khẩu phải từ 6 đến 20 ký tự")
    @NotBlank(message = "Mật khẩu không được để trống")
    private String newPassword;

    @NotBlank(message = "OTP không được để trống")
    private String code;
}
