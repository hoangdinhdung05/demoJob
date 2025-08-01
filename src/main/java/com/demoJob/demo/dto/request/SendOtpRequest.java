package com.demoJob.demo.dto.request;

import com.demoJob.demo.util.OtpType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendOtpRequest {
    private String email;
    private OtpType type;
}

