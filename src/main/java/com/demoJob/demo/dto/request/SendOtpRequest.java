package com.demoJob.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu gửi mã OTP")
public class SendOtpRequest {
    @Schema(description = "Email người dùng", example = "example@gmail.com")
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;
}
