package com.demoJob.demo.service;

import com.demoJob.demo.dto.response.Job.JobResponse;
import java.util.List;

public interface SaveJobService {

    /**
     * User lưu lại các Job mà mình quan tâm hoặc yêu thích
     */
    void saveJob(Long userId, Long jobId);

    /**
     * User xóa Job khỏi danh sách yêu thích
     */
    void deleteSaveJob(Long jobId);

    List<JobResponse> getSavedJobs(Long userId);

    boolean isJobSaved(Long userId, Long jobId);

}
