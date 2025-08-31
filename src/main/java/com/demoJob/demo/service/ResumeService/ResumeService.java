package com.demoJob.demo.service.ResumeService;

import com.demoJob.demo.dto.request.Admin.Resume.ResumeRequest;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeCreateResponse;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeResponse;
import com.demoJob.demo.dto.request.Resume.ResumeRequest;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeUpdateResponse;
import com.demoJob.demo.dto.response.Resume.ResumeCreateResponse;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.util.enums.ResumeStatus;

public interface ResumeService {

    /**
     * User apply vào job mình yêu cầu
     */
    ResumeCreateResponse createResume(ResumeRequest request);

    /**
     * HR hoặc Admin change status Resume của User
     */
    void changeStatus(long resumeId, ResumeStatus status);

    ResumeResponse getResumeById(long resumeId);

    PageResponse<?> getPageResume(int page, int size);
}
