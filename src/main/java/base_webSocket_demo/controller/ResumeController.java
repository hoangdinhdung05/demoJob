package base_webSocket_demo.controller;

import base_webSocket_demo.dto.request.Admin.Resume.ResumeRequest;
import base_webSocket_demo.dto.response.Admin.Resume.ResumeCreateResponse;
import base_webSocket_demo.dto.response.Admin.Resume.ResumeResponse;
import base_webSocket_demo.dto.response.Admin.Resume.ResumeUpdateResponse;
import base_webSocket_demo.dto.response.system.PageResponse;
import base_webSocket_demo.dto.response.system.ResponseData;
import base_webSocket_demo.dto.response.system.ResponseError;
import base_webSocket_demo.service.ResumeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/admin/create")
    public ResponseData<?> adminCreateResume(@RequestBody @Valid ResumeRequest request) {
        log.info("API admin create resume");
        try {
            ResumeCreateResponse response = resumeService.createResume(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), "API admin create resume successfully", response);
        } catch (Exception e) {
            log.error("API admin create resume error: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "API admin create resume fail");
        }
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

    @GetMapping("/admin/user/{userId}")
    public ResponseData<?> adminGetResumesByUserId(@PathVariable @Min(1) Long userId) {
        log.info("API admin get resumes by userId={}", userId);
        try {
            List<ResumeResponse> response = resumeService.getResumeByUserId(userId);
            return new ResponseData<>(HttpStatus.OK.value(), "API admin get resumes by user successfully", response);
        } catch (Exception e) {
            log.error("API admin get resumes by user error: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "API admin get resumes by user fail");
        }
    }

    @GetMapping("/admin/job/{jobId}")
    public ResponseData<?> adminGetResumesByJobId(@PathVariable @Min(1) Long jobId) {
        log.info("API admin get resumes by jobId={}", jobId);
        try {
            List<ResumeResponse> response = resumeService.getResumeByJobId(jobId);
            return new ResponseData<>(HttpStatus.OK.value(), "API admin get resumes by job successfully", response);
        } catch (Exception e) {
            log.error("API admin get resumes by job error: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "API admin get resumes by job fail");
        }
    }

    @GetMapping("/admin/getAll")
    public ResponseData<?> adminGetAllResumes() {
        log.info("API admin get all resumes");
        try {
            List<ResumeResponse> response = resumeService.getList();
            return new ResponseData<>(HttpStatus.OK.value(), "API admin get all resumes successfully", response);
        } catch (Exception e) {
            log.error("API admin get all resumes error: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "API admin get all resumes fail");
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
