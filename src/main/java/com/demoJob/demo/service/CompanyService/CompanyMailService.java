package com.demoJob.demo.service.CompanyService;

import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.helper.TemplateVariableMapper;
import com.demoJob.demo.service.EmailService;
import com.demoJob.demo.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyMailService {

    private final EmailUtil emailUtil;
    private final EmailService emailService;
    private final TemplateVariableMapper templateVariableMapper;

    @Value("${app.admin.email}")
    private String adminEmail;

    /**
     * Send notification to admin when new company registered
     */
    public void sendCompanyRegistrationNotification(Company company, User creator) {
        try {
            String subject = "New Company Registration: " + company.getName();
            Map<String, Object> variables = templateVariableMapper.toMapTemplate(company, creator);
            variables.put("createDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            emailService.sendTemplateEmailAsync(
                    adminEmail,
                    subject,
                    "company-registration-admin",
                    variables
            );

            log.info("Sent company registration email to admin for company {}" , company.getId());
        } catch (Exception e) {
            log.error("Error sending company registration email for company {}: {}", company.getId(), e.getMessage(), e);
        }
    }

    /**
     * Send approval notification to company owner
     */
    public void sendCompanyApprovalNotification(Company company, User owner) {
        try {
            String subject = "[APPROVED] Your Company Registration - " + company.getName();

            Map<String, Object> variables = templateVariableMapper.toMapTemplate(company, owner);
            variables.put("createDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            emailService.sendTemplateEmailAsync(
                    owner.getEmail(),
                    subject,
                    "company-approved-owner",
                    variables
            );

            log.info("Sent company approval email to owner {} for company {}", owner.getEmail(), company.getId());
        } catch (Exception e) {
            log.error("Error sending company approval email for company {}: {}", company.getId(), e.getMessage(), e);
        }
    }

    /**
     * Send rejection notification to company owner
     */
    public void sendCompanyRejectionNotification(Company company, User owner, String reason) {
        try {
            String subject = "[REJECTED] Your Company Registration - " + company.getName();

            Map<String, Object> variables = templateVariableMapper.toMapTemplate(company, owner, reason);
            variables.put("rejectionDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            variables.put("rejectionReason", reason != null && !reason.trim().isEmpty()
                    ? reason : "Please contact support for more details about the rejection.");

            emailService.sendTemplateEmailAsync(
                    owner.getEmail(),
                    subject,
                    "company-rejected-owner",
                    variables
            );

            log.info("Sent company rejection email to owner {} for company {}", owner.getEmail(), company.getId());
        } catch (Exception e) {
            log.error("Error sending company rejection email for company {}: {}", company.getId(), e.getMessage(), e);
        }
    }

    /**
     * Send pending notification to company owner
     */
    public void sendCompanyBackToPendingNotification(Company company, User owner) {
        try {
            String subject = "[UPDATE] Company Status Changed - " + company.getName();

            Map<String, Object> variables = templateVariableMapper.toMapTemplate(company, owner);
            variables.put("updateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            emailService.sendTemplateEmailAsync(
                    owner.getEmail(),
                    subject,
                    "company-back-to-pending",
                    variables
            );

            log.info("Sent company back to pending email to owner {} for company {}", owner.getEmail(), company.getId());
        } catch (Exception e) {
            log.error("Error sending company back to pending email for company {}: {}", company.getId(), e.getMessage(), e);
        }
    }
}
