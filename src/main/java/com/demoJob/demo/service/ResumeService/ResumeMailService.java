package com.demoJob.demo.service.ResumeService;

import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.Resume;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserCompany;
import com.demoJob.demo.exception.InvalidDataException;
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

    public void sendmailToHrOrOwner(Job job, User user, Resume resume) {
        try {
            UserCompany ownerCompany = userCompanyUtil.getOwnerCompany(job.getCompany().getId());
            User hrOrOwner = ownerCompany.getUser();

            String subject = "New Resume Submitted for Job: " + job.getName();

            // Build model for template
            Map<String, Object> model = new HashMap<>();
            model.put("hrName", getDisplayName(hrOrOwner));
            model.put("candidateName", getDisplayName(user));
            model.put("jobName", job.getName());
            model.put("candidateEmail", resume.getEmail());
            model.put("resumeUrl", resume.getUrl());
            model.put("fromEmail", user.getEmail());

            mailService.sendMail(
                    hrOrOwner.getEmail(),
                    subject,
                    "resume-notification", // template name
                    model
            );
            log.info("Sent resume email to HR {} for job {}", hrOrOwner.getEmail(), job.getId());

        } catch (InvalidDataException e) {
            log.warn("Cannot send resume email, no HR found for company {}", job.getCompany().getId());
        } catch (Exception e) {
            log.error("Error sending resume email for job {}: {}", job.getId(), e.getMessage(), e);
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
