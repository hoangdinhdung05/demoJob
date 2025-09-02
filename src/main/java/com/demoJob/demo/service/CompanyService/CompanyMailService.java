package com.demoJob.demo.service.CompanyService;

import com.demoJob.demo.config.AppProperties;
import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.service.EmailService;
import com.demoJob.demo.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final AppProperties appProperties;

    /**
     * Send notification to admin when new company registered
     */
    public void sendCompanyRegistrationNotification(Company company, User creator) {
        try {
            String subject = "New Company Registration: " + company.getName();

            Map<String, Object> variables = builderRegistration(company, creator);

            emailService.sendTemplateEmailAsync(
                    appProperties.getAdmin().getEmail(),
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

            Map<String, Object> variables = builderApproved(company, owner);

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

            Map<String, Object> variables = builderReject(company, owner, reason);

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

            Map<String, Object> variables = builderUpdateStatus(company, owner);

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

    //========== BUILDER ==========//

    private Map<String, Object> builderRegistration(Company company, User creator) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("companyId", company.getId());
        variables.put("companyName", company.getName());
        variables.put("companyEmail", company.getEmail());
        variables.put("companyPhone", company.getPhone() != null ? company.getPhone() : "N/A");
        variables.put("companyWebsite", company.getWebsite() != null ? company.getWebsite() : "N/A");
        variables.put("createDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        variables.put("creatorName", emailUtil.getDisplayName(creator));
        variables.put("creatorEmail", creator.getEmail());
        variables.put("approveUrl", appProperties.getFrontend() + "/admin/companies/" + company.getId() + "/approve");
        variables.put("rejectUrl", appProperties.getFrontend() + "/admin/companies/" + company.getId() + "/reject");
        variables.put("dashboardUrl", appProperties.getFrontend() + "/admin/companies");
        return variables;
    }

    private Map<String, Object> builderReject(Company company, User owner, String reason) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("companyName", company.getName());
        variables.put("ownerName", emailUtil.getDisplayName(owner));
        variables.put("rejectionReason", reason != null && !reason.trim().isEmpty()
                ? reason : "Please contact support for more details about the rejection.");
        variables.put("supportEmail", appProperties.getAdmin().getEmail());
        variables.put("reapplyUrl", appProperties.getFrontend() + "/company/register");
        return variables;
    }

    private Map<String, Object> builderUpdateStatus(Company company, User owner) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("companyName", company.getName());
        variables.put("companyEmail", company.getEmail());
        variables.put("companyPhone", company.getPhone() != null ? company.getPhone() : "Chưa cập nhật");
        variables.put("companyWebsite", company.getWebsite() != null ? company.getWebsite() : "Chưa cập nhật");
        variables.put("ownerName", emailUtil.getDisplayName(owner));
        variables.put("ownerEmail", owner.getEmail());
        variables.put("updateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        variables.put("dashboardUrl", appProperties.getFrontend() + "/company/dashboard");
        variables.put("supportEmail", appProperties.getAdmin().getEmail());
        variables.put("supportPhone", "1900-9099");
        return variables;
    }

    private Map<String, Object> builderApproved(Company company, User owner) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("companyName", company.getName());
        variables.put("ownerName", emailUtil.getDisplayName(owner));
        variables.put("loginUrl", appProperties.getFrontend() + "/login");
        variables.put("dashboardUrl", appProperties.getFrontend() + "/company/dashboard");
        variables.put("supportEmail", appProperties.getAdmin().getEmail());
        return variables;
    }
}
