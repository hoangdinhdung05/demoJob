package com.demoJob.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Yêu cầu đăng nhập")
public class LoginRequest {
    @Schema(example = "user123", description = "Tên đăng nhập của người dùng")
    @NotBlank(message = "Username cannot null")
    private String username;

    @Schema(example = "P@ssw0rd!", description = "Mật khẩu của người dùng")
    @NotBlank(message = "Password cannot null")
    private String password;
}