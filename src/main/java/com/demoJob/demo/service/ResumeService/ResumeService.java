package com.demoJob.demo.service.ResumeService;

import com.demoJob.demo.dto.request.Resume.ResumeRequest;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeUpdateResponse;
import com.demoJob.demo.dto.response.Resume.ResumeCreateResponse;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.Resume;
import java.util.List;

public interface ResumeService {

    /**
     * User apply vào job mình yêu cầu
     */
    ResumeCreateResponse createResume(ResumeRequest request);

    ResumeUpdateResponse updateResume(ResumeRequest request);

    void deleteResume(long resumeId);

    ResumeResponse getResumeById(long resumeId);

    PageResponse<?> getPageResume(int page, int size);
}
