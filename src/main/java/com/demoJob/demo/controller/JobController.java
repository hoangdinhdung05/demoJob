package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Job.JobRequest;
import com.demoJob.demo.dto.request.Job.JobStatusRequest;
import com.demoJob.demo.dto.response.Admin.Job.JobResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.dto.response.system.ResponseError;
import com.demoJob.demo.service.JobService;
import com.demoJob.demo.util.enums.JobStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobController {

    private final JobService jobService;

    @PostMapping("/admin/create")
    public ResponseData<?> adminCreateJob(@RequestBody @Valid JobRequest request) {
        log.info("API admin create job");

        try {
            JobResponse jobResponse = jobService.createJob(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Job created successfully", jobResponse);
        } catch (Exception e) {
            log.error("Job creation failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Job creation failed");
        }
    }

    /**
     * Get info job by jobId
     */
    @GetMapping("/{jobId}")
    public ResponseEntity<?> getJobById(@PathVariable Long jobId) {
        log.info("API get job by ID: {}", jobId);
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "API get info job by id successfully", jobService.getJobById(jobId)));    }

    /**
     * Admin và Owner dùng để update info của job
     */
    @PatchMapping("/{jobId}")
    public ResponseEntity<?> updateJob(@PathVariable Long jobId, @RequestBody @Valid JobRequest request) {
        log.info("API update job ID: {}", jobId);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "API update job successfully", jobService.updateJob(jobId, request)));
    }

    @DeleteMapping("/admin/{jobId}")
    public ResponseData<?> adminDeleteJob(@PathVariable @Min(1) Long jobId) {
        log.info("API admin delete job ID: {}", jobId);

        try {
            jobService.deleteJob(jobId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Job deleted successfully");
        } catch (Exception e) {
            log.error("Delete job failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete job failed");
        }
    }

    /**
     * Admin và Owner dùng để update status của job
     */
    @PatchMapping("/{jobId}/status")
    public ResponseEntity<?> adminChangeJobStatus(@PathVariable Long jobId,
                                                @RequestBody JobStatusRequest status) {
        log.info("API change status of job ID: {} to {}", jobId, status);
        jobService.updateJobStatus(jobId, status);
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "Company status updated successfully"));
    }

    /**
     * Get list info Job
     */
    @GetMapping
    public ResponseEntity<?> getListJob(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        log.info("API get list job");
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "API get list job successfully", jobService.getAllPage(page, size)));
    }
}
