package com.demoJob.demo.dto.request;

import com.demoJob.demo.util.OtpType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyOtpRequest {

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Mã OTP không được để trống")
    private String code;
    private OtpType type;
}

