package com.demoJob.demo.service;

import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.util.enums.SaveJobStatus;

public interface SaveJobService {

    /**
     * User lưu lại các Job mà mình quan tâm hoặc yêu thích
     */
    void saveJobOrUpdateStatus(Long userId, Long jobId, SaveJobStatus status);

    /**
     * Lấy ra list job mà User đã lưu
     */
    PageResponse<?> getAllSavedJobs(int page, int size);

    boolean isJobSaved(Long userId, Long jobId);

}
