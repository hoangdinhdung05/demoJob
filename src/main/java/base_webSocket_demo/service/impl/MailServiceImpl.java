package base_webSocket_demo.service.impl;

import base_webSocket_demo.dto.HtmlEmailTask;
import base_webSocket_demo.entity.User;
import base_webSocket_demo.service.MailService;
import base_webSocket_demo.util.OtpType;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final BlockingQueue<HtmlEmailTask> queue = new LinkedBlockingQueue<>();

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    @Value("${app.backend.url}")
    private String backendUrl;

    @PostConstruct
    private void initWorker() {
        Thread worker = new Thread(() -> {
            while (true) {
                try {
                    HtmlEmailTask task = queue.take();
                    processTask(task);
                } catch (InterruptedException e) {
                    log.warn("Email thread interrupted", e);
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Unexpected error in mail worker", e);
                }
            }
        }, "email-worker");

        worker.start();
    }

    private void processTask(HtmlEmailTask task) {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                sendHtmlEmail(task.getTo(), task.getSubject(), task.getTemplate(), task.getModel());
                log.info("Sent email to: {}", task.getTo());
                return;
            } catch (MailException e) {
                attempts++;
                log.warn("Failed to send mail to: {} | Attempt {}/{}", task.getTo(), attempts, MAX_RETRIES, e);
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            } catch (Exception e) {
                log.error("Unexpected error sending email to {}", task.getTo(), e);
            }
        }
        log.error("Gave up sending email to {} after {} attempts", task.getTo(), MAX_RETRIES);
    }

//    @Override
//    public void sendMail(String to, String subject, String template, Map<String, Object> model) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
//            Context context = new Context();
//            context.setVariables(model);
//            String html = templateEngine.process(template, context);
//
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(html, true);
//
//            mailSender.send(message);
//        } catch (Exception e) {
//            log.error("Lỗi gửi email: {}", e.getMessage(), e);
//            throw new RuntimeException("Không thể gửi email");
//        }
//    }
//
//    @Override
//    public void sendResetPasswordMail(User user, String token) {
//        String url = backendUrl + "/ott/reset-password?token=" + token;
//        Map<String, Object> model = Map.of(
//                "username", user.getUsername(),
//                "resetUrl", url
//        );
//        sendMail(user.getEmail(), "Khôi phục mật khẩu", "reset-password-template.html", model);
//    }
//
//    @Override
//    public void sendVerificationEmail(User user, String token) {
//        String url = backendUrl + "/ott/verify-email?token=" + token;
//        Map<String, Object> model = Map.of(
//                "username", user.getUsername(),
//                "verifyUrl", url
//        );
//        sendMail(user.getEmail(), "Xác minh tài khoản", "verify-email-template.html", model);
//    }
//
//    @Override
//    public void sendOtpMail(String to, String code, OtpType type) {
//        Map<String, Object> model = Map.of(
//                "otp", code,
//                "type", type.name()
//        );
//
//        sendMail(to, "[OTP] Xác thực hành động " + type.name(), "otp-template.html", model);
//    }
//

    private void sendHtmlEmail(String to, String subject, String template, Map<String, Object> model) {
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
            log.error("Error preparing or sending HTML email", e);
            throw new RuntimeException("Email preparation failed");
        }
    }

    // Gửi email với retry queue
    @Override
    public void sendMail(String to, String subject, String template, Map<String, Object> model) {
        HtmlEmailTask task = HtmlEmailTask.builder()
                .to(to)
                .subject(subject)
                .template(template)
                .model(model)
                .retryCount(0)
                .build();
        queue.add(task);
        log.info("Queued email to {}", to);
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

