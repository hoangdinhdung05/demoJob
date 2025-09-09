package com.demoJob.demo.dto.request;

import com.demoJob.demo.util.enums.OtpType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Yêu cầu xác thực mã OTP")
public class VerifyOtpRequest {

    @Schema(description = "Email của người dùng", example = "example@gmail.com")
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @Schema(description = "Mã OTP", example = "123456")
    @NotBlank(message = "Mã OTP không được để trống")
    private String code;
}

