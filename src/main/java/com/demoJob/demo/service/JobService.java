package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.Admin.Job.JobRequest;
import com.demoJob.demo.dto.response.Admin.Job.JobResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.util.enums.JobStatus;
import java.util.List;

public interface JobService {

    JobResponse createJob(JobRequest request);

    JobResponse updateJob(long jobId, JobRequest request);

    void deleteJob(long jobId);

    JobResponse changJobStatus(long jobId, JobStatus jobStatus);

    /**
     * Dùng chung logic cho cả User, Admin và người có Owner trong CTY
     * Nhưng User chỉ xem được những Job đã ACTIVE
     * Còn Admin và Owner(người tạo Job thì xem job)
     */
    JobResponse getJobById(Long id);

    /**
     * Tìm job thông qua Company
     */
    List<JobResponse> getJobByCompanyId(Long companyId);

    /**
     * User tìm job thông qua Skill
     */
    List<JobResponse> getJobBySkillName(String skillName);

    /**
     * Tìm job theo keyword
     */
    List<JobResponse> searchJobByName(String keyword);

    List<JobResponse> getAlls();

    /**
     * Dùng chung logic cho cả User, Admin và người có Owner trong CTY
     * Nhưng User chỉ xem được những Job đã ACTIVE
     * Còn Admin và Owner(người tạo Job thì xem all job)
     */
    PageResponse<?> getAllPage(int page, int size);

}
