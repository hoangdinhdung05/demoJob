package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.request.Job.JobRequest;
import com.demoJob.demo.dto.request.Job.JobUpdateRequest;
import com.demoJob.demo.dto.response.Admin.Job.JobResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.*;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.exception.NotFoundException;
import com.demoJob.demo.repository.*;
import com.demoJob.demo.security.SecurityUtils;
import com.demoJob.demo.service.JobService;
import com.demoJob.demo.service.MailService;
import com.demoJob.demo.util.enums.CompanyStatus;
import com.demoJob.demo.util.enums.JobStatus;
import com.demoJob.demo.util.enums.UserCompanyStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.demoJob.demo.mapper.JobMapper.buildJob;
import static com.demoJob.demo.mapper.JobMapper.toResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final UserCompanyRepository userCompanyRepository;

    /**
     * Admin và người tạo Job có thể tạo Job
     * Admin tạo job => ACTIVE luôn
     * User tạo Job => sendmail => Admin check => ACTIVE || REJECT
     *
     * @param request thông tin Job cần tạo
     */
    @Override
    public JobResponse createJob(JobRequest request) {

        // Get current user and get active company
        User user = getCurrentUser();
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");
        Company company = getCompanyAndCheckActive(request);

        // Validate permission
        checkPermissionCreateJob(user, company, isAdmin);

        // Get and validate skills
        List<Skill> skillList = getAllSkillById(request.getSkillIds());

        //save in DB
        Job job = buildJob(request, company, skillList, isAdmin);
        try {
            job = jobRepository.save(job);
        } catch (Exception e) {
            log.error("Failed to save job: {}", e.getMessage());
            throw new RuntimeException("Failed to create job", e);
        }

        // Send notification to admin
        if (!isAdmin) {
            try {
                mailService.sendJobRegistrationNotification(job, user);
                log.info("Notification sent to admin for job creation - jobName: {}, jobId: {}",
                        job.getName(), job.getId());
            } catch (Exception e) {
                log.error("Failed to send job registration notification for jobId: {}", job.getId(), e);
            }
        }

        log.info("Job created successfully - jobId: {}, status: {}, created by: {}",
                job.getId(), job.getStatus(), user.getEmail());

        return toResponse(job);
    }

    /**
     * Admin và người tạo Job có thể update Job
     *
     * @param request thông tin cần update
     */
    @Override
    public JobResponse updateJob(JobUpdateRequest request) {
        return null;
    }

    /**
     * Dùng chung cho Admin và nguời tạo Job
     * Soft delete
     *
     * @param jobId id Job cần xóa
     */
    @Override
    public void deleteJob(long jobId) {

    }

    /**
     * Admin xem xét đổi status của Job khi người tạo job sendmail đến
     * Có thể thay đổi mọi trạng thái
     *
     * @param jobId id Job cần thay đổi status
     * @param jobStatus status cần đổi
     */
    @Override
    public JobResponse changJobStatus(long jobId, JobStatus jobStatus) {
        return null;
    }

    /**
     * Dùng chung logic cho cả User, Admin và người có Owner trong CTY
     * Nhưng User chỉ xem được những Job đã ACTIVE
     * Còn Admin và Owner(người tạo Job thì xem job)
     *
     * @param id id Job cần lấy
     */
    @Override
    public JobResponse getJobById(Long id) {
        return null;
    }

    /**
     * Dùng chung logic cho cả User, Admin và người có Owner trong CTY
     * Nhưng User chỉ xem được những Job đã ACTIVE
     * Còn Admin và Owner(người tạo Job thì xem all job)
     *
     */
    @Override
    public PageResponse<?> getAllPage(int page, int size) {
        return null;
    }

    //========== PRIVATE METHOD ==========//

    private void checkPermissionCreateJob(User user, Company company, boolean isAdmin) {
        if (isAdmin) {
            log.debug("Admin user {} creating job for company {}", user.getEmail(), company.getName());
            return;
        }

        Optional<UserCompany> userCompanyOpt = userCompanyRepository
                .findByCompanyIdAndIsOwnerTrueAndStatus(company.getId(), UserCompanyStatus.ACTIVE);

        if (userCompanyOpt.isEmpty()) {
            throw new InvalidDataException("Company does not have an active owner");
        }

        UserCompany userCompany = userCompanyOpt.get();
        if (!userCompany.getUser().getId().equals(user.getId())) {
            throw new InvalidDataException("Bạn không có quyền tạo Job cho công ty này. Chỉ owner của công ty mới có thể tạo Job");
        }

        log.debug("User {} verified as owner of company {}", user.getEmail(), company.getName());
    }

    private Company getCompanyAndCheckActive(JobRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new NotFoundException("Company not found"));

        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new InvalidDataException("Không thể tạo Job khi Company chưa được ACTIVE. Vui lòng đợi Admin duyệt Company");
        }
        return company;
    }

    private List<Skill> getAllSkillById(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> uniqueSkillIds = skillIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (uniqueSkillIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Skill> skills = skillRepository.findAllById(uniqueSkillIds);

        if (skills.size() != uniqueSkillIds.size()) {
            List<Long> foundSkillIds = skills.stream()
                    .map(Skill::getId)
                    .toList();
            List<Long> missingSkillIds = uniqueSkillIds.stream()
                    .filter(id -> !foundSkillIds.contains(id))
                    .toList();

            throw new InvalidDataException("Skills not found: " + missingSkillIds);
        }

        return skills;
    }

    //Em thấy cái này dủng nhiều => chắc tách riêng sang utils
    private User getCurrentUser() {
        try {
            long userId = SecurityUtils.getCurrentUserId();
            return userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Current user not found with ID: " + userId));
        } catch (NumberFormatException e) {
            throw new SecurityException("Invalid user ID format", e);
        } catch (Exception e) {
            throw new SecurityException("Authentication error: " + e.getMessage(), e);
        }
    }
}
