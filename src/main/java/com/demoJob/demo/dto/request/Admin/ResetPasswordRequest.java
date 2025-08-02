package com.demoJob.demo.dto.request.Admin;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String newPassword;
    private String otp;
}
