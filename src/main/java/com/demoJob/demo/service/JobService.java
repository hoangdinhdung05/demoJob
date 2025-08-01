package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.Admin.Job.JobRequest;
import com.demoJob.demo.dto.response.Admin.Job.JobResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.util.JobStatus;
import java.util.List;

public interface JobService {

    JobResponse createJob(JobRequest request);

    JobResponse updateJob(long jobId, JobRequest request);

    void deleteJob(long jobId);

    JobResponse changJobStatus(long jobId, JobStatus jobStatus);

    JobResponse getById(Long id);

    List<JobResponse> getByCompanyId(Long companyId);

    List<JobResponse> getBySkillId(Long skillId);

    List<JobResponse> searchByName(String keyword);

    List<JobResponse> getAlls();

    PageResponse<?> getAllPage(int page, int size);

}
