package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Job.JobRequest;
import com.demoJob.demo.dto.request.Job.JobStatusRequest;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "JOB", description = "Quản lý Job")
public class JobController {

    private final JobService jobService;

    /**
     * Admin và Owner có thể tạo ra Job
     */
    @Operation(summary = "Tạo job mới", description = "Admin và Owner có thể tạo ra Job.")
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
    @Operation(summary = "Lấy thông tin job theo ID", description = "Lấy thông tin chi tiết của job theo ID.")
    @GetMapping("/{jobId}")
    public ResponseEntity<?> getJobById(@PathVariable Long jobId) {
        log.info("API get job by ID: {}", jobId);
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "API get info job by id successfully", jobService.getJobById(jobId)));    }

    /**
     * Admin và Owner dùng để update info của job
     */
    @Operation(summary = "Cập nhật job", description = "Admin và Owner dùng để update info của job.")
    @PatchMapping("/{jobId}")
    public ResponseEntity<?> updateJob(@PathVariable Long jobId, @RequestBody @Valid JobRequest request) {
        log.info("API update job ID: {}", jobId);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "API update job successfully", jobService.updateJob(jobId, request)));
    }

    /**
     * Admin hoặc Owner có thể xóa đi Job
     */
    @Operation(summary = "Xóa job", description = "Admin hoặc Owner có thể xóa đi Job.")
    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> deleteJob(@PathVariable Long jobId) {
        log.info("API delete job ID: {}", jobId);
        jobService.deleteJob(jobId);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Api delete job successfully"));
    }

    /**
     * Admin và Owner dùng để update status của job
     */
    @Operation(summary = "Cập nhật trạng thái job", description = "Admin và Owner dùng để update status của job.")
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
    @Operation(summary = "Lấy danh sách job", description = "Lấy danh sách tất cả job, có phân trang.")
    @GetMapping
    public ResponseEntity<?> getListJob(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        log.info("API get list job");
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "API get list job successfully", jobService.getAllPage(page, size)));
    }
}
