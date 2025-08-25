package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.request.Job.JobRequest;
import com.demoJob.demo.dto.response.Job.JobResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.*;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.exception.NotFoundException;
import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.Skill;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.exception.NotFoundException;
import com.demoJob.demo.mapper.JobMapper;
import com.demoJob.demo.repository.CompanyRepository;
import com.demoJob.demo.repository.JobRepository;
import com.demoJob.demo.repository.SkillRepository;
import com.demoJob.demo.repository.UserCompanyRepository;
import com.demoJob.demo.security.SecurityUtils;
import com.demoJob.demo.security.SecurityUtils;
import com.demoJob.demo.service.JobService;
import com.demoJob.demo.util.UserCompanyUtil;
import com.demoJob.demo.util.UserUtil;
import com.demoJob.demo.util.enums.CompanyStatus;
import com.demoJob.demo.util.enums.JobStatus;
import com.demoJob.demo.util.enums.UserCompanyStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.demoJob.demo.mapper.JobMapper.buildJob;
import static com.demoJob.demo.mapper.JobMapper.toResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;
    private final UserCompanyRepository userCompanyRepository;
    private final UserUtil userUtil;
    private final UserCompanyUtil userCompanyUtil;


    /**
     * Admin và người tạo Job có thể tạo Job
     * Admin tạo job => ACTIVE luôn
     *
     * @param request thông tin Job cần tạo
     */
    @Override
    public JobResponse createJob(JobRequest request) {

        // Get current user and get active company
        User user = userUtil.getCurrentUser();
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

        log.info("Job created successfully - jobId: {}, status: {}, created by: {}",
                job.getId(), job.getStatus(), user.getEmail());

        return toResponse(job);
    }

    @Override
    public JobResponse updateJob(long jobId, JobRequest request) {

        log.info("Updating job: {}", request.getName());

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<Skill> skills = fetchSkillsByIds(request.getSkillIds());

        job.setName(request.getName());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setQuantity(request.getQuantity());
        job.setLevel(request.getLevel());
        job.setDescription(request.getDescription());
        job.setStartDate(request.getStartDate());
        job.setEndDate(request.getEndDate());
        job.setStatus(request.getStatus());
        job.setCompany(company);
        job.setSkills(skills);

        Job jobUpdate = jobRepository.save(job);

        log.info("Update a job successfully with job id={}", jobId);

        return convertToJob(jobUpdate);
    }

    /**
     * Admin hoặc Owner có thể xóa đi Job
     */
    @Override
    public void deleteJob(long jobId) {

        Job job = getJobByIdOrThrow(jobId);
        Company company = job.getCompany();
        User currentUser = userUtil.getCurrentUser();

        if (!SecurityUtils.hasRole("ADMIN") && !SecurityUtils.hasRole("MANAGER") && !userCompanyUtil.isOwner(currentUser, company)) {
            throw new InvalidDataException("Bạn không đủ quyền hạn xóa jobId: " + jobId);
        }

        job.setStatus(JobStatus.DELETE);
        jobRepository.save(job);

        log.info("Delete job with jobId={} successfully", jobId);
    }

    @Override
    public JobResponse changJobStatus(long jobId, JobStatus jobStatus) {

        log.warn("Change job status with job ID: {}", jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setStatus(jobStatus);

        job = jobRepository.save(job);

        return convertToJob(job);
    }

    /**
     * Dùng chung logic cho cả User, Admin và người có Owner trong CTY
     * Nhưng User chỉ xem được những Job đã ACTIVE
     * Còn Admin và Owner(người tạo Job thì xem job)
     */
    @Override
    public JobResponse getJobById(Long id) {
        Job job = getJobByPermission(id);
        log.info("Get info job successfully with jobId={}", id);
        return toResponse(job);
    }

    @Override
    public List<JobResponse> getByCompanyId(Long companyId) {

        log.info("Getting jobs for company ID: {}", companyId);

        List<Job> jobs = jobRepository.findAll()
                .stream()
                .filter(job -> job.getCompany().getId().equals(companyId))
                .toList();

        return jobs.stream().map(this::convertToJob).toList();
    }

    @Override
    public List<JobResponse> getBySkillId(Long skillId) {

        log.info("Getting jobs for skill ID: {}", skillId);

        List<Job> jobs = jobRepository.findAll()
                .stream()
                .filter(job -> job.getSkills().stream().anyMatch(skill -> skill.getId().equals(skillId)))
                .toList();

        return jobs.stream().map(this::convertToJob).toList();
    }

    @Override
    public List<JobResponse> searchByName(String keyword) {

        log.info("Searching jobs by name containing: {}", keyword);

        return jobRepository.findAll()
                .stream()
                .filter(job -> job.getName().toLowerCase().contains(keyword.toLowerCase()))
                .map(this::convertToJob)
                .toList();
    }

    @Override
    public List<JobResponse> getAlls() {

        log.info("Fetching all jobs");

        return jobRepository.findAll()
                .stream()
                .map(this::convertToJob)
                .toList();
    }

    /**
     * Get list info Job
     */
    @Override
    public PageResponse<?> getAllPage(int page, int size) {

        Page<Job> jobPage;

        if (checkRole()) {
            jobPage = jobRepository.findAll(PageRequest.of(page, size));
        } else {
            jobPage = jobRepository.findByStatus(JobStatus.ACTIVE, PageRequest.of(page, size));
        }

        List<JobResponse> list = jobPage.stream()
                .map(JobMapper::toResponse)
                .toList();

        return PageResponse.<JobResponse>builder()
                .page(jobPage.getNumber())
                .size(jobPage.getSize())
                .total(jobPage.getTotalElements())
                .items(list)
                .build();
    }

    //================//==================//

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

    private boolean checkRole() {
        return SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("MANAGER");
    }

    private Job getJobByIdOrThrow(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found with id: " + jobId));
    }

    private Job getJobByPermission(Long jobId) {
        Job job = getJobByIdOrThrow(jobId);

        if (job.getStatus() == JobStatus.ACTIVE) return job;
        if ((checkRole())) return job;
        User user = SecurityUtils.getCurrentUserDetails().getUser();
        if (user.getUsername().equals(job.getCreatedBy())) return job;

        throw new NotFoundException("Job not found");
    }

    private void checkPermissionCreateJob(User user, Company company, boolean isAdmin) {
        if (isAdmin) {
            log.debug("Admin user {} creating job for company {}", user.getEmail(), company.getName());
            return;
        }

        UserCompany userCompany = userCompanyRepository
                .findByCompanyIdAndIsOwnerTrueAndStatus(company.getId(), UserCompanyStatus.ACTIVE)
                .orElseThrow(() -> new InvalidDataException("Company does not have an active owner"));

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
}
