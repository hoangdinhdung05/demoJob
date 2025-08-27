package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.Admin.Resume.ResumeRequest;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeCreateResponse;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeResponse;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeUpdateResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.Resume;
import java.util.List;

public interface ResumeService {

    ResumeCreateResponse createResume(ResumeRequest request);

    ResumeUpdateResponse updateResume(ResumeRequest request);

    /**
     * HR hoặc Admin xóa đi Resume của User
     */
    void deleteResume(long resumeId);

    ResumeResponse getResumeById(long resumeId);

    PageResponse<?> getPageResume(int page, int size);
}
