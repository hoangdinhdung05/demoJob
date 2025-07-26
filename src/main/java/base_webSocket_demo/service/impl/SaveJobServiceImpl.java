package base_webSocket_demo.service.impl;

import base_webSocket_demo.dto.response.Admin.Job.CompanyJobResponse;
import base_webSocket_demo.dto.response.Admin.Job.JobResponse;
import base_webSocket_demo.dto.response.Admin.SkillResponse;
import base_webSocket_demo.entity.Job;
import base_webSocket_demo.entity.SaveJob;
import base_webSocket_demo.repository.JobRepository;
import base_webSocket_demo.repository.SaveJobRepository;
import base_webSocket_demo.repository.UserRepository;
import base_webSocket_demo.service.SaveJobService;
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
