package com.demoJob.demo.dto.response;

import com.demoJob.demo.util.OtpType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyOtpRequest {
    private String email;
    private String code;
    private OtpType type;
}

