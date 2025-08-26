package com.demoJob.demo.mapper;

import com.demoJob.demo.dto.response.Resume.ResumeResponse;
import com.demoJob.demo.entity.Resume;

public class ResumeMapper {
    public static ResumeResponse toResponse(Resume resume) {
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
