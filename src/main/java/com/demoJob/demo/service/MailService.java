package com.demoJob.demo.service;

import com.demoJob.demo.entity.User;
import com.demoJob.demo.util.OtpType;
import java.util.Map;

public interface MailService {
    void sendMail(String to, String subject, String template, Map<String, Object> model);
    void sendResetPasswordMail(User user, String token);
    void sendVerificationEmail(User user, String token);
    void sendOtpMail(String to, String code, OtpType type);
}
