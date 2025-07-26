package base_webSocket_demo.service.impl;

import base_webSocket_demo.entity.User;
import base_webSocket_demo.service.MailService;
import base_webSocket_demo.util.OtpType;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.backend.url}")
    private String backendUrl;

    @Override
    public void sendMail(String to, String subject, String template, Map<String, Object> model) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            Context context = new Context();
            context.setVariables(model);
            String html = templateEngine.process(template, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Lỗi gửi email: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể gửi email");
        }
    }

    @Override
    public void sendResetPasswordMail(User user, String token) {
        String url = backendUrl + "/ott/reset-password?token=" + token;
        Map<String, Object> model = Map.of(
                "username", user.getUsername(),
                "resetUrl", url
        );
        sendMail(user.getEmail(), "Khôi phục mật khẩu", "reset-password-template.html", model);
    }

    @Override
    public void sendVerificationEmail(User user, String token) {
        String url = backendUrl + "/ott/verify-email?token=" + token;
        Map<String, Object> model = Map.of(
                "username", user.getUsername(),
                "verifyUrl", url
        );
        sendMail(user.getEmail(), "Xác minh tài khoản", "verify-email-template.html", model);
    }

    @Override
    public void sendOtpMail(String to, String code, OtpType type) {
        Map<String, Object> model = Map.of(
                "otp", code,
                "type", type.name()
        );

        sendMail(to, "[OTP] Xác thực hành động " + type.name(), "otp-template.html", model);
    }


}

