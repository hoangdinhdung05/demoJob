package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.request.Admin.Resume.ResumeRequest;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeCreateResponse;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.Resume;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.exception.NotFoundException;
import com.demoJob.demo.repository.JobRepository;
import com.demoJob.demo.repository.ResumeRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.SecurityUtils;
import com.demoJob.demo.service.ResumeService.ResumeMailService;
import com.demoJob.demo.service.ResumeService.ResumeService;
import com.demoJob.demo.util.UserCompanyUtil;
import com.demoJob.demo.util.UserUtil;
import com.demoJob.demo.util.enums.ResumeStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final UserUtil userUtil;
    private final UserCompanyUtil userCompanyUtil;
    private final ResumeMailService resumeMailService;

    @Override
    public ResumeCreateResponse createResume(ResumeRequest request) {
        log.info("Creating resume for userId={} and jobId={}", request.getUserId(), request.getJobId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new EntityNotFoundException("Job not found"));

        Resume resume = Resume.builder()
                .email(request.getEmail())
                .url(request.getUrl())
                .user(user)
                .job(job)
                .status(ResumeStatus.PENDING)
                .build();

        Resume saved = resumeRepository.save(resume);

        return ResumeCreateResponse.builder()
                .id(saved.getId())
                .email(user.getEmail())
                .createdAt(saved.getCreatedAt())
                .createdBy(saved.getCreatedBy())
                .build();
    }

    /**
     * HR hoặc Admin change status Resume của User
     */
    @Override
    public void changeStatus(long resumeId, ResumeStatus status) {
        log.info("Changing resume status with id={} to {}", resumeId, status);
        Resume resume = getResumeOrThrow(resumeId);

        checkPermission(resume.getJob());
        validateTransition(resume.getStatus(), status);

        resume.setStatus(status);
        resumeRepository.save(resume);

        // send mail to User
        switch (status) {
            case APPROVED -> resumeMailService.sendMailResumeApproved(resume);
            case REJECTED -> resumeMailService.sendMailResumeRejected(resume); // dùng overload
            case DELETED -> log.info("Resume {} marked as DELETED, no mail sent", resumeId);
            default -> log.info("No mail action for status {}", status);
        }

        log.info("Changed resume status successfully: id={}, newStatus={}", resumeId, status);
    }


    @Override
    public ResumeResponse getResumeById(long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found"));
        return toResponse(resume);
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

    //========== PRIVATE METHOD ==========//
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

    private Resume getResumeOrThrow(long resumeId) {
        return resumeRepository.findById(resumeId)
                .orElseThrow(() -> new NotFoundException("Resume not found"));
    }

    private void checkPermission(Job job) {
        User currentUser = userUtil.getCurrentUser();
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");

        if (isAdmin) return;
        if (!userCompanyUtil.isOwnerOfCompany(currentUser, job.getCompany())) {
            throw new InvalidDataException("Bạn không có quyền thực hiện thao tác này");
        }
    }

    private void validateTransition(ResumeStatus current, ResumeStatus target) {
        if (current == ResumeStatus.DELETED) {
            throw new InvalidDataException("Resume đã bị xoá, không thể thay đổi trạng thái");
        }
        if (current == ResumeStatus.APPROVED && target == ResumeStatus.REJECTED) {
            throw new InvalidDataException("Resume đã duyệt không thể bị từ chối");
        }
    }
}
