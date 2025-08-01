package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.response.Admin.Job.CompanyJobResponse;
import com.demoJob.demo.dto.response.Admin.Job.JobResponse;
import com.demoJob.demo.dto.response.Admin.SkillResponse;
import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.SaveJob;
import com.demoJob.demo.repository.JobRepository;
import com.demoJob.demo.repository.SaveJobRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.service.SaveJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SaveJobServiceImpl implements SaveJobService {

    private final SaveJobRepository saveJobRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Override
    public void saveJob(Long userId, Long jobId) {
        if (saveJobRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new RuntimeException("Job already saved");
        }

        SaveJob saveJob = SaveJob.builder()
                .user(userRepository.findById(userId).orElseThrow())
                .job(jobRepository.findById(jobId).orElseThrow())
                .build();

        saveJobRepository.save(saveJob);
        log.info("User {} saved job {}", userId, jobId);
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
}
