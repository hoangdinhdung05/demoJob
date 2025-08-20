package com.demoJob.demo.service;

import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.util.enums.OtpType;
import java.util.Map;

public interface MailService {
    /**
     * Send general email with template
     */
    void sendMail(String to, String subject, String template, Map<String, Object> model);

    /**
     * Send OTP email
     */
    void sendOtpMail(String to, String code, OtpType type);

    /**
     * Send notification to admin when new company registered
     */
    void sendCompanyRegistrationNotification(Company company, User creator);

    /**
     * Send approval notification to company owner
     */
    void sendCompanyApprovalNotification(Company company, User owner);

    /**
     * Send rejection notification to company owner
     */
    void sendCompanyRejectionNotification(Company company, User owner, String reason);

    /**
     * Send pending notification to company owner
     */
    void sendCompanyBackToPendingNotification(Company company, User owner);
}
