package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.response.VerifyOtpRequest;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.util.OtpType;

public interface OtpService {
    void sendOtp(User user, SendOtpRequest request);

    void sendOtp(String email, OtpType type);

    boolean verifyOtp(VerifyOtpRequest request);
}

