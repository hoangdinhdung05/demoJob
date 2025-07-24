package base_webSocket_demo.service.impl;

import base_webSocket_demo.dto.request.Admin.Resume.ResumeRequest;
import base_webSocket_demo.dto.response.Admin.Resume.ResumeCreateResponse;
import base_webSocket_demo.dto.response.Admin.Resume.ResumeResponse;
import base_webSocket_demo.dto.response.Admin.Resume.ResumeUpdateResponse;
import base_webSocket_demo.dto.response.system.PageResponse;
import base_webSocket_demo.entity.Job;
import base_webSocket_demo.entity.Resume;
import base_webSocket_demo.entity.User;
import base_webSocket_demo.repository.JobRepository;
import base_webSocket_demo.repository.ResumeRepository;
import base_webSocket_demo.repository.UserRepository;
import base_webSocket_demo.service.ResumeService;
import base_webSocket_demo.util.ResumeStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

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
}
