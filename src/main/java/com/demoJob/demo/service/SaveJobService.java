package com.demoJob.demo.service;

import com.demoJob.demo.dto.response.Admin.Job.JobResponse;
import java.util.List;

public interface SaveJobService {

    void saveJob(Long userId, Long jobId);

    void deleteSaveJob(Long userId, Long jobId);

    List<JobResponse> getSavedJobs(Long userId);

    boolean isJobSaved(Long userId, Long jobId);

}
