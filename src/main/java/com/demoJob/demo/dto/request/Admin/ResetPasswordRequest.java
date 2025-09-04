package com.demoJob.demo.dto.request.Admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Yêu cầu đặt lại mật khẩu")
public class ResetPasswordRequest {

    @Schema(description = "Key dùng để xác thực việc đặt lại mật khẩu", example = "verifyKey123")
    @NotBlank(message = "Verify key không được để trống")
    private String verifyKey;

    @Schema(description = "Mật khẩu mới", example = "NewP@ssw0rd!")
    @NotBlank(message = "Password mới không được để trống")
    private String newPassword;

    @Schema(description = "Xác nhận mật khẩu mới", example = "NewP@ssw0rd!")
    @NotBlank(message = "Xác nhận password không được để trống")
    private String confirmPassword;
}

