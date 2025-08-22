package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Job.JobRequest;
import com.demoJob.demo.dto.response.Job.JobResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
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

    /**
     * Admin và Owner có thể tạo ra Job
     */
    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody @Valid JobRequest request) {
        log.info("API create job with jobName={}", request.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseData<>(HttpStatus.CREATED.value(),
                        "API create job successfully", jobService.createJob(request)));
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
