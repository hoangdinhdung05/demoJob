package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Resume.ResumeRequest;
import com.demoJob.demo.dto.response.Admin.Resume.ResumeResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.dto.response.system.ResponseError;
import com.demoJob.demo.service.ResumeService.ResumeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    public ResponseEntity<?> createResume(@RequestBody @Valid ResumeRequest request) {
        log.info("API create resume");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseData<>(HttpStatus.CREATED.value(),
                        "API create resume successfully", resumeService.createResume(request)));
    }

    @PutMapping("/admin/update")
    public ResponseData<?> adminUpdateResume(@RequestBody @Valid ResumeRequest request) {
        log.info("API admin update resume");
        try {
            ResumeUpdateResponse response = resumeService.updateResume(request);
            return new ResponseData<>(HttpStatus.OK.value(), "API admin update resume successfully", response);
        } catch (Exception e) {
            log.error("API admin update resume error: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "API admin update resume fail");
        }
    }

    @DeleteMapping("/admin/{resumeId}")
    public ResponseData<?> adminDeleteResume(@PathVariable @Min(1) Long resumeId) {
        log.info("API admin delete resume, id={}", resumeId);
        try {
            resumeService.deleteResume(resumeId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "API admin delete resume successfully");
        } catch (Exception e) {
            log.error("API admin delete resume error: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "API admin delete resume fail");
        }
    }

    @GetMapping("/admin/{resumeId}")
    public ResponseData<?> adminGetResumeById(@PathVariable @Min(1) Long resumeId) {
        log.info("API admin get resume by id={}", resumeId);
        try {
            ResumeResponse response = resumeService.getResumeById(resumeId);
            return new ResponseData<>(HttpStatus.OK.value(), "API admin get resume by id successfully", response);
        } catch (Exception e) {
            log.error("API admin get resume by id error: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "API admin get resume by id fail");
        }
    }

    @GetMapping("/admin/page")
    public ResponseData<?> adminGetPageResumes(@RequestParam int page, @RequestParam int size) {
        log.info("API admin get paged resumes, page={}, size={}", page, size);
        try {
            PageResponse<?> response = resumeService.getPageResume(page, size);
            return new ResponseData<>(HttpStatus.OK.value(), "API admin get paged resumes successfully", response);
        } catch (Exception e) {
            log.error("API admin get paged resumes error: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "API admin get paged resumes fail");
        }
    }
}
