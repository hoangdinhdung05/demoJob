package com.demoJob.demo.service.ResumeService;

import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.Resume;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserCompany;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.helper.TemplateVariableMapper;
import com.demoJob.demo.service.EmailService;
import com.demoJob.demo.util.EmailUtil;
import com.demoJob.demo.util.UserCompanyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeMailService {

    private final EmailService emailService;
    private final UserCompanyUtil userCompanyUtil;
    private final EmailUtil emailUtil;
    private final TemplateVariableMapper templateVariableMapper;

    public void sendmailToHrOrOwner(Job job, User user, Resume resume) {
        try {
            UserCompany ownerCompany = userCompanyUtil.getOwnerCompany(job.getCompany().getId());
            User hrOrOwner = ownerCompany.getUser();

            String subject = "New Resume Submitted for Job: " + job.getName();
            Map<String, Object> variables = templateVariableMapper.toMapTemplate(hrOrOwner, user, job, resume);

            emailService.sendTemplateEmailAsync(
                    new String[]{hrOrOwner.getEmail()},
                    subject,
                    "resume-notification",
                    variables
            );

            log.info("Sent resume email to HR {} for job {}", hrOrOwner.getEmail(), job.getId());
        } catch (InvalidDataException e) {
            log.warn("Cannot send resume email, no HR found for company {}", job.getCompany().getId());
        } catch (Exception e) {
            log.error("Error sending resume email for job {}: {}", job.getId(), e.getMessage(), e);
        }
    }

    // --- HR/Owner -> User ---
    public void sendMailResumeApproved(Resume resume) {
        try {
            User candidate = resume.getUser();
            Job job = resume.getJob();

            String subject = "Your application for " + job.getName() + " has been approved";

            Map<String, Object> model = new HashMap<>();
            model.put("candidateName", emailUtil.getDisplayName(candidate));
            model.put("jobName", job.getName());
            model.put("companyName", job.getCompany().getName());

            emailService.sendTemplateEmailAsync(
                    candidate.getEmail(),
                    subject,
                    "resume-approved",
                    model
            );
            log.info("Sent APPROVED mail to {} for job {}", candidate.getEmail(), job.getId());

        } catch (Exception e) {
            log.error("Error sending APPROVED mail for resume {}: {}", resume.getId(), e.getMessage(), e);
        }
    }

    public void sendMailResumeRejected(Resume resume) {
        // overload mặc định, không có lý do
        sendMailResumeRejected(resume, null);
    }

    public void sendMailResumeRejected(Resume resume, String reason) {
        try {
            User candidate = resume.getUser();
            Job job = resume.getJob();

            String subject = "Your application for " + job.getName() + " has been rejected";

            Map<String, Object> model = new HashMap<>();
            model.put("candidateName", emailUtil.getDisplayName(candidate));
            model.put("jobName", job.getName());
            model.put("companyName", job.getCompany().getName());
            model.put("reason", reason != null ? reason : "No specific reason provided");

            emailService.sendTemplateEmailAsync(
                    candidate.getEmail(),
                    subject,
                    "resume-rejected",
                    model
            );
            log.info("Sent REJECTED mail to {} for job {}", candidate.getEmail(), job.getId());

        } catch (Exception e) {
            log.error("Error sending REJECTED mail for resume {}: {}", resume.getId(), e.getMessage(), e);
        }
    }
}
