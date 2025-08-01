package com.demoJob.demo.dto.request;

public record ResetPasswordRequest(String token, String newPassword) {}
