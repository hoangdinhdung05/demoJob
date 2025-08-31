package com.demoJob.demo.service.ResumeService;

import com.demoJob.demo.dto.request.Resume.ResumeRequest;
import com.demoJob.demo.dto.response.Resume.ResumeCreateResponse;
import com.demoJob.demo.dto.response.Resume.ResumeResponse;
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

    /**
     * Lấy thông tin resume theo idResume
     */
    ResumeResponse getResumeById(long resumeId);

    /**
     * Lấy toàn bộ resume có phân trang
     */
    PageResponse<?> getAllResumes(int page, int size);
}
