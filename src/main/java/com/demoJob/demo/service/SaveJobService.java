package com.demoJob.demo.service;

import com.demoJob.demo.dto.response.Admin.Job.JobResponse;
import java.util.List;

public interface SaveJobService {

    /**
     * User lưu lại các Job mà mình quan tâm hoặc yêu thích
     */
    void saveJob(Long userId, Long jobId);

    void deleteSaveJob(Long userId, Long jobId);

    List<JobResponse> getSavedJobs(Long userId);

    boolean isJobSaved(Long userId, Long jobId);

}
