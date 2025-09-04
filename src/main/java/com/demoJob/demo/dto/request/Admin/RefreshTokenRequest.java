package com.demoJob.demo.dto.request.Admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Yêu cầu làm mới token")
public class RefreshTokenRequest {

    @Schema(description = "Refresh token của người dùng", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @NotBlank(message = "Refresh token không được để trống")
    private String refreshToken;
}