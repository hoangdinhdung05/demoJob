package base_webSocket_demo.service;

import base_webSocket_demo.dto.request.SendOtpRequest;
import base_webSocket_demo.dto.response.VerifyOtpRequest;

public interface OtpService {
    void sendOtp(SendOtpRequest request);
    boolean verifyOtp(VerifyOtpRequest request);
}

