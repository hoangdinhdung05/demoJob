package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.request.VerifyOtpRequest;
import com.demoJob.demo.entity.User;

public interface OtpService {
    void sendOtp(User user, SendOtpRequest request);

    boolean verifyOtp(VerifyOtpRequest request);
}

