package com.demoJob.demo.service.ResumeService;

import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.Resume;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.service.MailService;
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

    private final MailService mailService;
    private final UserCompanyUtil userCompanyUtil;

    // --- HR/Owner -> User ---
    public void sendMailResumeApproved(Resume resume) {
        try {
            User candidate = resume.getUser();
            Job job = resume.getJob();

            String subject = "Your application for " + job.getName() + " has been approved";

            Map<String, Object> model = new HashMap<>();
            model.put("candidateName", getDisplayName(candidate));
            model.put("jobName", job.getName());
            model.put("companyName", job.getCompany().getName());

            mailService.sendMail(
                    candidate.getEmail(),
                    subject,
                    "resume-approved", // template name
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
            model.put("candidateName", getDisplayName(candidate));
            model.put("jobName", job.getName());
            model.put("companyName", job.getCompany().getName());
            model.put("reason", reason != null ? reason : "No specific reason provided");

            mailService.sendMail(
                    candidate.getEmail(),
                    subject,
                    "resume-rejected", // template name
                    model
            );
            log.info("Sent REJECTED mail to {} for job {}", candidate.getEmail(), job.getId());

        } catch (Exception e) {
            log.error("Error sending REJECTED mail for resume {}: {}", resume.getId(), e.getMessage(), e);
        }
    }

    public String getDisplayName(User user) {
        if (user.getFirstName() != null || user.getLastName() != null) {
            return (user.getFirstName() == null ? "" : user.getFirstName()) +
                    " " +
                    (user.getLastName() == null ? "" : user.getLastName());
        }
        return user.getUsername() != null ? user.getUsername() : user.getEmail();
    }
}
