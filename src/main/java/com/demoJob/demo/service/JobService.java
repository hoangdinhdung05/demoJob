package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.Job.JobRequest;
import com.demoJob.demo.dto.request.Job.JobUpdateRequest;
import com.demoJob.demo.dto.response.Admin.Job.JobResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.util.enums.JobStatus;

public interface JobService {

    /**
     * Admin và người tạo Job có thể tạo Job
     * Admin tạo job => ACTIVE luôn
     * User tạo Job => sendmail => Admin check => ACTIVE || REJECT
     */
    JobResponse createJob(JobRequest request);

    /**
     * Admin và người tạo Job có thể update Job
     */
    JobResponse updateJob(JobUpdateRequest request);

    /**
     * Dùng chung cho Admin và nguời tạo Job
     * Soft delete
     */
    void deleteJob(long jobId);

    /**
     * Admin xem xét đổi status của Job khi người tạo job sendmail đến
     * Có thể thay đổi mọi trạng thái
     */
    JobResponse changJobStatus(long jobId, JobStatus jobStatus);

    /**
     * Dùng chung logic cho cả User, Admin và người có Owner trong CTY
     * Nhưng User chỉ xem được những Job đã ACTIVE
     * Còn Admin và Owner(người tạo Job thì xem job)
     */
    JobResponse getJobById(Long id);

    /**
     * Dùng chung logic cho cả User, Admin và người có Owner trong CTY
     * Nhưng User chỉ xem được những Job đã ACTIVE
     * Còn Admin và Owner(người tạo Job thì xem all job)
     */
    PageResponse<?> getAllPage(int page, int size);

}
