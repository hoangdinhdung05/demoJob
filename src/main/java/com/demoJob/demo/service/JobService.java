package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.Job.JobRequest;
import com.demoJob.demo.dto.response.Job.JobResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.util.enums.JobStatus;
import java.util.List;

public interface JobService {

    /**
     * Admin và người tạo Job có thể tạo Job
     * Admin tạo job => ACTIVE luôn
     */
    JobResponse createJob(JobRequest request);

    JobResponse updateJob(long jobId, JobRequest request);

    /**
     * Admin hoặc Owner có thể xóa đi Job
     */
    void deleteJob(long jobId);

    JobResponse changJobStatus(long jobId, JobStatus jobStatus);

    /**
     * Dùng chung logic cho cả User, Admin và người có Owner trong CTY
     * Nhưng User chỉ xem được những Job đã ACTIVE
     * Còn Admin và Owner(người tạo Job thì xem job)
     */
    JobResponse getJobById(Long id);

    List<JobResponse> getByCompanyId(Long companyId);

    List<JobResponse> getBySkillId(Long skillId);

    List<JobResponse> searchByName(String keyword);

    List<JobResponse> getAlls();

    /**
     * Dùng chung logic cho cả User, Admin và người có Owner trong CTY
     * Nhưng User chỉ xem được những Job đã ACTIVE
     * Còn Admin và Owner(người tạo Job thì xem all job)
     */
    PageResponse<?> getAllPage(int page, int size);

}
