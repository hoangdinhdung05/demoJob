package base_webSocket_demo.service;

import base_webSocket_demo.entity.Job;
import base_webSocket_demo.entity.User;
import base_webSocket_demo.entity.UserCompany;
import base_webSocket_demo.repository.JobRepository;
import base_webSocket_demo.repository.UserCompanyRepository;
import base_webSocket_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplyJobService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final MailService mailService;
    private final UserCompanyRepository userCompanyRepository;

    public void applyJob(Long jobId, String cvUrl, User authenticatedUser) {
        User user = userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // ✅ Tìm HR theo companyId của Job
        List<UserCompany> hrList = userCompanyRepository.findByCompanyId(job.getCompany().getId());
        if (hrList.isEmpty()) {
            throw new RuntimeException("No HR found for this company");
        }
        User hr = hrList.get(0).getUser();
        String hrEmail = hr.getEmail();

        // ✅ Gửi mail
        Map<String, Object> model = new HashMap<>();
        model.put("userName", user.getLastName());
        model.put("userEmail", user.getEmail());
        model.put("cvUrl", cvUrl);
        model.put("jobTitle", job.getName());
        model.put("jobLink", "https://yourdomain.com/jobs/" + job.getId());

        mailService.sendHtmlMail(
                hrEmail,
                "[demoJob] Ứng viên mới cho công việc " + job.getName(),
                "apply_notify",
                model
        );

        log.info("User {} applied for job {} and notified HR: {}", user.getId(), jobId, hrEmail);
    }

}
