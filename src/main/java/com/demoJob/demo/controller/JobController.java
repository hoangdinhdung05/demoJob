package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Job.JobRequest;
import com.demoJob.demo.dto.response.Admin.Job.JobResponse;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Slf4j
@Validated
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody @Valid JobRequest request) {
        log.info("API create job with jobName={}", request.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseData<>(HttpStatus.CREATED.value(),
                        "API create job successfully", jobService.createJob(request)));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<?> getJobById(@PathVariable Long jobId) {
        log.info("API get info job by id");
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "API get info job by id successfully", jobService.getJobById(jobId)));
    }

    @GetMapping
    public ResponseEntity<?> getAllJobs(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        log.info("API get list job");
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "API get list job successfully", jobService.getAllPage(page, size)));
    }
}
