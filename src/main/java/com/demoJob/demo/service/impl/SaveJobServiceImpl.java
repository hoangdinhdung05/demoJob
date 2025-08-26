package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.response.Job.CompanyJobResponse;
import com.demoJob.demo.dto.response.Job.JobResponse;
import com.demoJob.demo.dto.response.Admin.SkillResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.SaveJob;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.DuplicateResourceException;
import com.demoJob.demo.exception.NotFoundException;
import com.demoJob.demo.mapper.JobMapper;
import com.demoJob.demo.exception.NotFoundException;
import com.demoJob.demo.repository.JobRepository;
import com.demoJob.demo.repository.SaveJobRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.service.SaveJobService;
import com.demoJob.demo.util.enums.JobStatus;
import com.demoJob.demo.util.enums.SaveJobStatus;
import jakarta.persistence.EntityNotFoundException;
import com.demoJob.demo.util.UserUtil;
import com.demoJob.demo.util.enums.JobStatus;
import com.demoJob.demo.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SaveJobServiceImpl implements SaveJobService {

    private final SaveJobRepository saveJobRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final UserUtil userUtil;

    /**
     * User lưu lại các Job mà mình quan tâm hoặc yêu thích
     */
    @Override
    public void saveJob(Long userId, Long jobId) {
        //check exists
        if (validExistsJobAndActive(userId, jobId)) return;

        //Valid user and job
        var user = getUserOrThrow(userId);
        var job = getJobOrThrow(jobId);

        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new NotFoundException("Job not found");
        }

        SaveJob saveJob = SaveJob.builder()
                .user(user)
                .job(job)
                .status(SaveJobStatus.ACTIVE)
                .build();

        saveJobRepository.save(saveJob);
        log.info("User {} saved job {} successfully", userId, jobId);
    }

    /**
     * User xóa Job khỏi danh sách yêu thích
     */
    @Override
    public void deleteSaveJob(Long jobId) {
        User user = userUtil.getCurrentUser();
        //Không cần load entity
        int update = saveJobRepository.softDeleteByUserIdAndJobId(user.getId(), jobId);
        if (update == 0) throw new NotFoundException("Save job not found for user");

        log.info("User={} removed saved job={}", user.getId(), jobId);
    }

    /**
     * Lấy ra list job mà User đã lưu
     */
    @Override
    public PageResponse<?> getAllSavedJobs(int page, int size) {

        User user = userUtil.getCurrentUser();

        Page<SaveJob> jobPage = saveJobRepository.findAllByUserId(PageRequest.of(page, size), user.getId());

        List<JobResponse> responses = jobPage.getContent().stream()
                .map(saveJob -> JobMapper.toResponse(saveJob.getJob()))
                .collect(Collectors.toList());

        return PageResponse.<JobResponse>builder()
                .page(jobPage.getNumber())
                .size(jobPage.getSize())
                .total(jobPage.getTotalElements())
                .items(responses)
                .build();
    }

    @Override
    public boolean isJobSaved(Long userId, Long jobId) {
        return saveJobRepository.existsByUserIdAndJobId(userId, jobId);
    }

    private JobResponse convertToJob(SaveJob saveJob) {
        Job job = saveJob.getJob();
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

    //========== PRIVATE METHOD ==========//
    private Job getJobOrThrow(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found"));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private boolean validExistsJobAndActive(Long userId, Long jobId) {
        Optional<SaveJob> existsJob = saveJobRepository.findByUserIdAndJobId(userId, jobId);

        if (existsJob.isPresent()) {
            SaveJob saveJob = existsJob.get();
            if (saveJob.getStatus() == SaveJobStatus.ACTIVE) {
                throw new DuplicateResourceException("Job đã nằm trong danh sách");
            }

            if (saveJob.getStatus() == SaveJobStatus.DELETE) {
                saveJob.setStatus(SaveJobStatus.ACTIVE);
                saveJobRepository.save(saveJob);
                log.info("User {} re-saved job {} successfully", userId, jobId);
                return true;
            }
        }
        return false;
    }
}
