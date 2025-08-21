package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.request.Admin.Job.JobRequest;
import com.demoJob.demo.dto.response.Admin.Job.CompanyJobResponse;
import com.demoJob.demo.dto.response.Admin.Job.JobResponse;
import com.demoJob.demo.dto.response.Admin.SkillResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.Skill;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.NotFoundException;
import com.demoJob.demo.mapper.JobMapper;
import com.demoJob.demo.repository.CompanyRepository;
import com.demoJob.demo.repository.JobRepository;
import com.demoJob.demo.repository.SkillRepository;
import com.demoJob.demo.security.SecurityUtils;
import com.demoJob.demo.service.JobService;
import com.demoJob.demo.util.enums.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import static com.demoJob.demo.mapper.JobMapper.toResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    @Override
    public JobResponse createJob(JobRequest request) {

        log.info("Create a job with name={}", request.getName());

        //check cty
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<Skill> skills = fetchSkillsByIds(request.getSkillIds());

        Job job = Job.builder()
                .name(request.getName())
                .location(request.getLocation())
                .salary(request.getSalary())
                .quantity(request.getQuantity())
                .level(request.getLevel())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .company(company)
                .skills(skills)
                .build();

        job = jobRepository.save(job);

        log.info("Create a job successfully with job id={}", job.getId());

        return convertToJob(job);

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

    @Override
    public void deleteJob(long jobId) {

        log.warn("Deleting job ID: {}", jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setStatus(JobStatus.INACTIVE);

        jobRepository.save(job);
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
        Job job = checkAvtiveJob(id);
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

    private JobResponse convertToJob(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .name(job.getName())
                .location(job.getLocation())
                .salary(job.getSalary())
                .quantity(job.getQuantity())
                .level(job.getLevel())
                .description(job.getDescription())
                .startDate(job.getStartDate())
                .endDate(job.getEndDate())
                .status(job.getStatus())
                .company(CompanyJobResponse.builder()
                        .id(job.getCompany().getId())
                        .name(job.getCompany().getName())
                        .build())
                .skills(job.getSkills().stream()
                        .map(skill -> SkillResponse.builder()
                                .id(skill.getId())
                                .name(skill.getName())
                                .description(skill.getDescription())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

    private List<Skill> fetchSkillsByIds(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) return List.of();

        List<Skill> skills = skillRepository.findAllById(skillIds);
        if (skills.size() != skillIds.size()) {
            throw new RuntimeException("Some skill don`t exists");
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

    private Job checkAvtiveJob(Long jobId) {
        Job job = getJobByIdOrThrow(jobId);

        if (job.getStatus() == JobStatus.ACTIVE) return job;
        if ((checkRole())) return job;
        User user = SecurityUtils.getCurrentUserDetails().getUser();
        if (user.getUsername().equals(job.getCreatedBy())) return job;

        throw new NotFoundException("Job not found");
    }
}
