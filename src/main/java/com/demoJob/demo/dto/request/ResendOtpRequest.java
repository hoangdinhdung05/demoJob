package com.demoJob.demo.dto.request;

import com.demoJob.demo.util.enums.OtpType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu resend OTP")
public class ResendOtpRequest {

    @Schema(description = "Email người dùng", example = "example@gmail.com")
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @Schema(description = "Loại OTP", example = "REGISTER")
    @NotNull(message = "Loại OTP không được để trống")
    private OtpType type;
}
