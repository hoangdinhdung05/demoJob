package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.request.Resume.ResumeRequest;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeUpdateResponse;
import com.demoJob.demo.dto.response.Resume.ResumeCreateResponse;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.Resume;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.DuplicateResourceException;
import com.demoJob.demo.exception.NotFoundException;
import com.demoJob.demo.repository.JobRepository;
import com.demoJob.demo.repository.ResumeRepository;
import com.demoJob.demo.service.ResumeService.ResumeMailService;
import com.demoJob.demo.service.ResumeService.ResumeService;
import com.demoJob.demo.util.UserUtil;
import com.demoJob.demo.util.enums.JobStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import static com.demoJob.demo.mapper.ResumeMapper.toCreateResponse;
import static com.demoJob.demo.mapper.ResumeMapper.toEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final UserUtil userUtil;
    private final ResumeMailService resumeMailService;

    /**
     * User apply vào job mình yêu cầu
     */
    @Override
    public ResumeCreateResponse createResume(ResumeRequest request) {
        User user = userUtil.getCurrentUser();

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new EntityNotFoundException("Job not found"));

        validateRequestCreate(user, job);

        log.info("Create resume with userId={} and jobId={}", user.getId(), job.getId());

        Resume resume = toEntity(request, user, job);
        resumeRepository.save(resume);

        log.info("Create resume with userId={} and jobId={} successfully", user.getId(), job.getId());

        //SendMail to HR
        resumeMailService.sendmailToHrOrOwner(job, user, resume);

        return toCreateResponse(resume);
    }

    @Override
    public ResumeUpdateResponse updateResume(ResumeRequest request) {
        log.info("Updating resume for userId={} and jobId={}", request.getUserId(), request.getJobId());

        Resume resume = resumeRepository.findByUserIdAndJobId(request.getUserId(), request.getJobId())
                .orElseThrow(() -> new EntityNotFoundException("Resume not found"));

        resume.setEmail(request.getEmail());
        resume.setUrl(request.getUrl());
        resume.setUpdatedAt(LocalDateTime.now());
        resume.setUpdatedBy(resume.getUser().getUsername());

        Resume updated = resumeRepository.save(resume);

        return  ResumeUpdateResponse.builder()
                .id(updated.getId())
                .updatedAt(updated.getUpdatedAt())
                .updatedBy(updated.getUpdatedBy())
                .build();
    }

    @Override
    public void deleteResume(long resumeId) {
        log.info("Deleting resume with id={}", resumeId);
        if (!resumeRepository.existsById(resumeId)) {
            throw new EntityNotFoundException("Resume not found");
        }
        resumeRepository.deleteById(resumeId);
    }

    @Override
    public ResumeResponse getResumeById(long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found"));
        return toResponse(resume);
    }

    @Override
    public List<ResumeResponse> getResumeByUserId(long userId) {
        List<Resume> resumes = resumeRepository.findByUserId(userId);
        return resumes.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ResumeResponse> getResumeByJobId(long jobId) {
        List<Resume> resumes = resumeRepository.findByJobId(jobId);
        return resumes.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ResumeResponse> getList() {
        List<Resume> resumes = resumeRepository.findAll();
        return resumes.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public PageResponse<?> getPageResume(int page, int size) {
        Page<Resume> resumePage = resumeRepository.findAll(PageRequest.of(page, size));

        List<ResumeResponse> list = resumePage.stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<ResumeResponse>builder()
                .page(resumePage.getNumber() + 1)
                .size(resumePage.getSize())
                .total(resumePage.getTotalElements())
                .items(list)
                .build();
    }

    @Override
    public boolean checkResumeExistsByUserAndJob(Resume resume) {
        return resumeRepository.existsByUserIdAndJobId(resume.getUser().getId(), resume.getJob().getId());
    }

    private ResumeResponse toResponse(Resume resume) {
        return ResumeResponse.builder()
                .id(resume.getId())
                .email(resume.getEmail())
                .url(resume.getUrl())
                .status(resume.getStatus())
                .createdAt(resume.getCreatedAt())
                .updatedAt(resume.getUpdatedAt())
                .createdBy(resume.getCreatedBy())
                .updatedBy(resume.getUpdatedBy())
                .companyName(resume.getJob().getCompany().getName())
                .user(ResumeResponse.UserResume.builder()
                        .id(resume.getUser().getId())
                        .name(resume.getUser().getFirstName() + " " + resume.getUser().getLastName())
                        .build())
                .job(ResumeResponse.JobResume.builder()
                        .id(resume.getJob().getId())
                        .name(resume.getJob().getName())
                        .build())
                .build();
    }

    //========== PRIVATE MODTHOD ==========//

    private void validateRequestCreate(User user, Job job) {
        if (resumeRepository.existsByUserIdAndJobId(user.getId(), job.getId())) {
            throw new DuplicateResourceException("You already applied for this job");

        }

        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new NotFoundException("Job not found");
        }
    }
}
