package com.demoJob.demo.mapper;

import com.demoJob.demo.dto.request.Resume.ResumeRequest;
import com.demoJob.demo.dto.response.Resume.ResumeCreateResponse;
import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.Resume;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.util.enums.ResumeStatus;

public class ResumeMapper {

    public static Resume toEntity(ResumeRequest request, User user, Job job) {
        return Resume.builder()
                .email(request.getEmail())
                .url(request.getUrl())
                .user(user)
                .job(job)
                .status(ResumeStatus.PENDING)
                .build();
    }

    public static ResumeCreateResponse toCreateResponse(Resume resume) {
        return ResumeCreateResponse.builder()
                .id(resume.getId())
                .email(resume.getEmail())
                .createdAt(resume.getCreatedAt())
                .createdBy(resume.getCreatedBy())
                .build();
    }

}
