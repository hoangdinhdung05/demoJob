package base_webSocket_demo.service;

import base_webSocket_demo.entity.User;
import base_webSocket_demo.util.OtpType;
import java.util.Map;

public interface MailService {
    void sendMail(String to, String subject, String template, Map<String, Object> model);
    void sendResetPasswordMail(User user, String token);
    void sendVerificationEmail(User user, String token);
    void sendOtpMail(String to, String code, OtpType type);
}
