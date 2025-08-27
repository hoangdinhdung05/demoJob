package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.Admin.Resume.ResumeRequest;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeCreateResponse;
import com.demoJob.demo.dto.response.Resume.ResumeResponse;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeUpdateResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.Resume;
import java.util.List;

public interface ResumeService {

    ResumeCreateResponse createResume(ResumeRequest request);

    ResumeUpdateResponse updateResume(ResumeRequest request);

    void deleteResume(long resumeId);

    /**
     * Lấy thông tin resume theo idResume
     */
    ResumeResponse getResumeById(long resumeId);

    List<ResumeResponse> getResumeByUserId(long userId);

    List<ResumeResponse> getResumeByJobId(long jobId);

    List<ResumeResponse> getList();

    /**
     * Lấy toàn bộ resume có phân trang
     */
    PageResponse<?> getAllResumes(int page, int size);

    boolean checkResumeExistsByUserAndJob(Resume resume);

    PageResponse<?> getPageResume(int page, int size);
}
