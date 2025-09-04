package com.demoJob.demo.dto.request;

import com.demoJob.demo.util.enums.OtpType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Yêu cầu gửi mã OTP")
public class SendOtpRequest {
    @Schema(description = "Email người dùng", example = "example@gmail.com")
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;
}
