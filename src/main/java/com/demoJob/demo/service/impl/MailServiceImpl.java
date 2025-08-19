package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.HtmlEmailTask;
import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.service.MailService;
import com.demoJob.demo.util.enums.OtpType;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.admin.email}")
    private String adminEmail;

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
    public void sendOtpMail(String to, String code, OtpType type) {
        Map<String, Object> model = Map.of(
                "otp", code,
                "type", type.name()
        );
        sendMail(to, "[OTP] Xác thực hành động " + type.name(), "otp-template.html", model);
    }

    /**
     * Send notification to admin when new company registered
     */
    @Override
    public void sendCompanyRegistrationNotification(Company company, User creator) {
        try {
            Map<String, Object> model = buildCompanyNotificationModel(company, creator);

            String subject = "[ACTION REQUIRED] New Company Registration - " + company.getName();
            String template = "company-registration-admin.html";

            sendMail(adminEmail, subject, template, model);
            log.info("Company registration notification queued for admin - Company: {}", company.getName());

        } catch (Exception e) {
            log.error("Failed to send company registration notification for company: {}", company.getId(), e);
        }
    }

    /**
     * Send approval notification to company owner
     */
    @Override
    public void sendCompanyApprovalNotification(Company company, User owner) {
        try {
            Map<String, Object> model = buildCompanyApprovalModel(company, owner);

            String subject = "[APPROVED] Your Company Registration - " + company.getName();

            sendMail(owner.getEmail(), subject, "company-approved-owner.html", model);
            log.info("Company approval notification queued for: {} - Company: {}", owner.getEmail(), company.getName());

        } catch (Exception e) {
            log.error("Failed to send company approval notification for company: {}", company.getId(), e);
        }
    }

    /**
     * Send rejection notification to company owner
     */
    @Override
    public void sendCompanyRejectionNotification(Company company, User owner, String reason) {
        try {
            Map<String, Object> model = buildCompanyRejectionModel(company, owner, reason);

            String subject = "[REJECTED] Your Company Registration - " + company.getName();

            sendMail(owner.getEmail(), subject, "company-rejected-owner.html", model);
            log.info("Company rejection notification queued for: {} - Company: {}", owner.getEmail(), company.getName());

        } catch (Exception e) {
            log.error("Failed to send company rejection notification for company: {}", company.getId(), e);
        }
    }

    // ========== PRIVATE HELPER METHODS ==========

    private Map<String, Object> buildCompanyNotificationModel(Company company, User creator) {
        Map<String, Object> model = new HashMap<>();
        model.put("companyId", company.getId());
        model.put("companyName", company.getName());
        model.put("companyEmail", company.getEmail());
        model.put("companyPhone", company.getPhone() != null ? company.getPhone() : "N/A");
        model.put("companyWebsite", company.getWebsite() != null ? company.getWebsite() : "N/A");
        model.put("createDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        model.put("creatorName", getDisplayName(creator));
        model.put("creatorEmail", creator.getEmail());
        model.put("approveUrl", frontendUrl + "/admin/companies/" + company.getId() + "/approve");
        model.put("rejectUrl", frontendUrl + "/admin/companies/" + company.getId() + "/reject");
        model.put("dashboardUrl", frontendUrl + "/admin/companies");
        return model;
    }

    private Map<String, Object> buildCompanyApprovalModel(Company company, User owner) {
        Map<String, Object> model = new HashMap<>();
        model.put("companyName", company.getName());
        model.put("ownerName", getDisplayName(owner));
        model.put("loginUrl", frontendUrl + "/login");
        model.put("dashboardUrl", frontendUrl + "/company/dashboard");
        model.put("supportEmail", adminEmail);
        return model;
    }

    private Map<String, Object> buildCompanyRejectionModel(Company company, User owner, String reason) {
        Map<String, Object> model = new HashMap<>();
        model.put("companyName", company.getName());
        model.put("ownerName", getDisplayName(owner));
        model.put("rejectionReason", reason != null && !reason.trim().isEmpty()
                ? reason : "Please contact support for more details about the rejection.");
        model.put("supportEmail", adminEmail);
        model.put("reapplyUrl", frontendUrl + "/company/register");
        return model;
    }

    private String getDisplayName(User user) {
        if (user.getFirstName() != null && !user.getLastName().trim().isEmpty()) {
            return user.getFirstName() + user.getLastName();
        }
        return user.getUsername() != null ? user.getUsername() : user.getEmail();
    }
}