package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Admin.Job.JobRequest;
import com.demoJob.demo.dto.response.Admin.Job.JobResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.dto.response.system.ResponseError;
import com.demoJob.demo.service.JobService;
import com.demoJob.demo.util.JobStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/admin/{jobId}")
    public ResponseData<?> adminGetJobById(@PathVariable @Min(1) Long jobId) {
        log.info("API admin get job by ID: {}", jobId);

        try {
            JobResponse job = jobService.getById(jobId);
            return new ResponseData<>(HttpStatus.OK.value(), "Get job by ID successfully", job);
        } catch (Exception e) {
            log.error("Get job by ID failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get job by ID failed");
        }
    }

    @PatchMapping("/admin/{jobId}")
    public ResponseData<?> adminUpdateJob(@PathVariable @Min(1) Long jobId, @RequestBody @Valid JobRequest request) {
        log.info("API admin update job ID: {}", jobId);

        try {
            JobResponse response = jobService.updateJob(jobId, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Job updated successfully", response);
        } catch (Exception e) {
            log.error("Update job failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update job failed");
        }
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

    @PatchMapping("/admin/change-status/{jobId}")
    public ResponseData<?> adminChangeJobStatus(@PathVariable @Min(1) Long jobId,
                                                @RequestParam JobStatus status) {
        log.info("API admin change status of job ID: {} to {}", jobId, status);

        try {
            JobResponse job = jobService.changJobStatus(jobId, status);
            return new ResponseData<>(HttpStatus.OK.value(), "Job status updated successfully", job);
        } catch (Exception e) {
            log.error("Change job status failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change job status failed");
        }
    }

    @GetMapping("/admin/getAll")
    public ResponseData<?> adminGetAllJobs(@RequestParam int page, @RequestParam int size) {
        log.info("API admin get all jobs with pagination");

        try {
            PageResponse<?> response = jobService.getAllPage(page, size);
            return new ResponseData<>(HttpStatus.OK.value(), "Get jobs successfully", response);
        } catch (Exception e) {
            log.error("Get all jobs failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get all jobs failed");
        }
    }
}
