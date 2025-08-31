package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.response.Job.JobResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.SaveJob;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.DuplicateResourceException;
import com.demoJob.demo.exception.NotFoundException;
import com.demoJob.demo.mapper.JobMapper;
import com.demoJob.demo.repository.JobRepository;
import com.demoJob.demo.repository.SaveJobRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.service.SaveJobService;
import com.demoJob.demo.util.enums.JobStatus;
import com.demoJob.demo.util.enums.SaveJobStatus;
import jakarta.persistence.EntityNotFoundException;
import com.demoJob.demo.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final UserUtil userUtil;

    /**
     * User lưu lại các Job mà mình quan tâm hoặc yêu thích
     */
    @Override
    public void saveJobOrUpdateStatus(Long userId, Long jobId, SaveJobStatus status) {
        //Valid user and job
        var user = getUserOrThrow(userId);
        var job = getJobOrThrow(jobId);
        
        if (job.getStatus() != JobStatus.ACTIVE && status == SaveJobStatus.ACTIVE) {
            throw new NotFoundException("Job not found");
        }

        //Get save job
        SaveJob saveJob = getSaveJob(userId, jobId);

        //check exists job và xem cần tích lại không
        if (checkExistsAndReSave(userId, jobId, status, saveJob)) return;

        if (saveJob != null) {
            validSaveJobStatus(status, saveJob);
            // Change status (ACTIVE hoặc DELETE)
            changeStatusSaveJob(status, saveJob);
            log.info("User {} updated job {} status to {}", userId, jobId, status);
        } else {
            if (status == SaveJobStatus.ACTIVE) {
                // Tạo mới
                createSaveJob(user, job);
                log.info("User {} saved job {} successfully", userId, jobId);
            } else {
                throw new NotFoundException("Save job not found for user");
            }
        }
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

    //========== PRIVATE METHOD ==========//
    private Job getJobOrThrow(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found"));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private SaveJob findSaveJob(Long jobId, List<SaveJob> jobs) {
        return jobs.stream()
                .filter(sj -> sj.getJob().getId().equals(jobId))
                .findFirst()
                .orElse(null);
    }

    private SaveJob getSaveJob(Long userId, Long jobId) {
        List<SaveJob> jobs = saveJobRepository.findAllByUserId(userId);
        return findSaveJob(jobId, jobs);
    }

    private void createSaveJob(User user, Job job) {
        SaveJob saveJob;
        saveJob = SaveJob.builder()
                .user(user)
                .job(job)
                .status(SaveJobStatus.ACTIVE)
                .build();
        saveJobRepository.save(saveJob);
    }

    private void validSaveJobStatus(SaveJobStatus status, SaveJob saveJob) {
        if (saveJob.getStatus() == status) {
            if (status == SaveJobStatus.ACTIVE) {
                throw new DuplicateResourceException("Job đã nằm trong danh sách");
            } else {
                throw new NotFoundException("Job đã bị xóa trước đó");
            }
        }
    }

    private void changeStatusSaveJob(SaveJobStatus status, SaveJob saveJob) {
        saveJob.setStatus(status);
        saveJobRepository.save(saveJob);
    }

    private void reSavedJobInSaveJob(Long userId, Long jobId, SaveJob saveJob) {
        changeStatusSaveJob(SaveJobStatus.ACTIVE, saveJob);
        log.info("User {} re-saved job {} successfully", userId, jobId);
    }

    private boolean checkExistsAndReSave(Long userId, Long jobId, SaveJobStatus status, SaveJob saveJob) {
        if (status == SaveJobStatus.ACTIVE && saveJob != null) {
            if (saveJob.getStatus() == SaveJobStatus.ACTIVE) {
                throw new DuplicateResourceException("Job đã nằm trong danh sách");
            } else if (saveJob.getStatus() == SaveJobStatus.DELETE) {
                reSavedJobInSaveJob(userId, jobId, saveJob);
                return true;
            }
        }
        return false;
    }
}
