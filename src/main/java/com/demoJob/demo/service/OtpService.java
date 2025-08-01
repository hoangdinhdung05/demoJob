package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.response.VerifyOtpRequest;

public interface OtpService {
    void sendOtp(SendOtpRequest request);
    boolean verifyOtp(VerifyOtpRequest request);
}

