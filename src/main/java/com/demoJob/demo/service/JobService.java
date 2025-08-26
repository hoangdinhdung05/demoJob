package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.Job.JobRequest;
import com.demoJob.demo.dto.request.Job.JobStatusRequest;
import com.demoJob.demo.dto.response.Job.JobResponse;
import com.demoJob.demo.dto.response.system.PageResponse;

public interface JobService {

    /**
     * Admin và người tạo Job có thể tạo Job
     * Admin tạo job => ACTIVE luôn
     */
    JobResponse createJob(JobRequest request);

    /**
     * Admin và owner update info cho Job
     */
    JobResponse updateJob(Long jobId, JobRequest request);

    /**
     * Admin hoặc Owner có thể xóa đi Job
     */
    void deleteJob(long jobId);

    /**
     * Thay đổi trạng thái của job (Admin và owner)
     */
    void updateJobStatus(long jobId, JobStatusRequest jobStatus);

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
