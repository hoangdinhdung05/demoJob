package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.request.Admin.Resume.ResumeRequest;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeCreateResponse;
import com.demoJob.demo.dto.response.Resume.ResumeResponse;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeUpdateResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.Resume;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.mapper.ResumeMapper;
import com.demoJob.demo.repository.JobRepository;
import com.demoJob.demo.repository.ResumeRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.SecurityUtils;
import com.demoJob.demo.service.ResumeService;
import com.demoJob.demo.util.UserCompanyUtil;
import com.demoJob.demo.util.UserUtil;
import com.demoJob.demo.util.enums.ResumeStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import static com.demoJob.demo.mapper.ResumeMapper.toResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final UserUtil userUtil;
    private final UserCompanyUtil userCompanyUtil;

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

    /**
     * Lấy thông tin resume theo idResume
     */
    @Override
    public ResumeResponse getResumeById(long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found"));

        boolean isCreatorResume = checkCreatorResume(resume);

        if (!isCreatorResume) {
            checkPermission(resume.getJob());
        }

        log.info("Get resume by resumeId={} successfully", resumeId);

        return toResponse(resume);
    }

    /**
     * Lấy toàn bộ resume có phân trang
     */
    @Override
    public PageResponse<?> getAllResumes(int page, int size) {

        User currentUser = userUtil.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);

        Page<Resume> resumePage;

        if (SecurityUtils.hasRole("ADMIN")) {
            resumePage = resumeRepository.findAll(pageable);
        } else if (userCompanyUtil.isOwner(currentUser)) {
            List<Long> companyIds = userCompanyUtil.getCompaniesOfUser(currentUser).stream()
                    .map(c -> c.getCompany().getId())
                    .toList();

            if (companyIds.isEmpty()) {
                resumePage = Page.empty(pageable);
            } else {
                resumePage = resumeRepository.findByJob_Company_IdIn(companyIds, pageable);
            }
        } else {
            resumePage = resumeRepository.findByCreatedBy(currentUser.getUsername(), pageable);
        }

        List<ResumeResponse> list = resumePage.stream()
                .map(ResumeMapper::toResponse)
                .toList();

        log.info("Get all resumes successfully");

        return PageResponse.<ResumeResponse>builder()
                .page(resumePage.getNumber())
                .size(resumePage.getSize())
                .total(resumePage.getTotalElements())
                .items(list)
                .build();
    }

    //========== PRIVATE METHOD =========//
    private void checkPermission(Job job) {
        User currentUser = userUtil.getCurrentUser();
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");

        if (isAdmin) return;
        if (!userCompanyUtil.isOwnerOfCompany(currentUser, job.getCompany())) {
            throw new InvalidDataException("Bạn không có quyền thực hiện thao tác này");
        }
    }

    private boolean checkCreatorResume(Resume resume) {
        User currentUser = userUtil.getCurrentUser();
        return resume.getCreatedBy().equals(currentUser.getUsername());
    }
}
