package com.demoJob.demo.dto.request;

import jakarta.validation.constraints.Email;

public record ForgotPasswordRequest(
        @Email(message = "Email không chuẩn định dạng")
        String email) {}