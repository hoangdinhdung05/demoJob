package com.demoJob.demo.service.ResumeService;

import com.demoJob.demo.dto.request.Resume.ResumeRequest;
import com.demoJob.demo.dto.request.Admin.Resume.ResumeRequest;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeCreateResponse;
import com.demoJob.demo.dto.response.Resume.ResumeResponse;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeUpdateResponse;
import com.demoJob.demo.dto.response.Resume.ResumeCreateResponse;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeResponse;
import com.demoJob.demo.dto.response.system.PageResponse;

public interface ResumeService {

    /**
     * User apply vào job mình yêu cầu
     */
    ResumeCreateResponse createResume(ResumeRequest request);

    ResumeUpdateResponse updateResume(ResumeRequest request);

    void deleteResume(long resumeId);

    /**
     * Lấy thông tin resume theo idResume
     */
    ResumeResponse getResumeById(long resumeId);

    /**
     * Lấy toàn bộ resume có phân trang
     */
    PageResponse<?> getAllResumes(int page, int size);
}
