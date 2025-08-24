package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.response.Admin.Job.CompanyJobResponse;
import com.demoJob.demo.dto.response.Admin.Job.JobResponse;
import com.demoJob.demo.dto.response.Admin.SkillResponse;
import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.SaveJob;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.DuplicateResourceException;
import com.demoJob.demo.repository.JobRepository;
import com.demoJob.demo.repository.SaveJobRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.service.SaveJobService;
import com.demoJob.demo.util.enums.SaveJobStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        SaveJob saveJob = SaveJob.builder()
                .user(user)
                .job(job)
                .status(SaveJobStatus.ACTIVE)
                .build();

        saveJobRepository.save(saveJob);
        log.info("User {} saved job {} successfully", userId, jobId);
    }

    @Override
    public void deleteSaveJob(Long userId, Long jobId) {
        saveJobRepository.findByUserIdAndJobId(userId, jobId)
                .ifPresent(saveJobRepository::delete);
        log.info("User {} removed saved job {}", userId, jobId);
    }

    @Override
    public List<JobResponse> getSavedJobs(Long userId) {
        return saveJobRepository.findAllByUserId(userId).stream()
                .map(this::convertToJob)
                .collect(Collectors.toList());
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
    private boolean checkExistsJobInCategory(Long userId, Long jobId) {
        return saveJobRepository.existsByUserIdAndJobId(userId, jobId);
    }

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
