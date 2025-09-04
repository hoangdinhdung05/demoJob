package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Resume.ResumeRequest;
import com.demoJob.demo.dto.response.Resume.ResumeResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.dto.response.Resume.ResumeUpdateResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.dto.response.system.ResponseError;
import com.demoJob.demo.service.ResumeService.ResumeService;
import com.demoJob.demo.util.enums.ResumeStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "RESUME", description = "Quản lý hồ sơ ứng tuyển")
public class ResumeController {

    private final ResumeService resumeService;

    /**
     * User apply vào job mình yêu cầu
     */
    @Operation(summary = "User nộp hồ sơ vào job", description = "User nộp hồ sơ vào job mình yêu cầu.")
    @PostMapping
    public ResponseEntity<?> createResume(@RequestBody @Valid ResumeRequest request) {
        log.info("API create resume");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseData<>(HttpStatus.CREATED.value(),
                        "API create resume successfully", resumeService.createResume(request)));
    }

    /**
     * HR hoặc Admin change status Resume của User
     */
    @Operation(summary = "Thay đổi trạng thái hồ sơ", description = "HR hoặc Admin thay đổi trạng thái hồ sơ của User.")
    @PatchMapping("/{resumeId}")
    public ResponseEntity<?> changeStatus(@PathVariable Long resumeId, @RequestParam ResumeStatus status) {
        log.info("API change status resume, id={}", resumeId);
        resumeService.changeStatus(resumeId, status);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.NO_CONTENT.value(),
                "Api change status resume successfully"));
    }

    /**
     * - Admin: được phép xem tất cả
     * - Owner: xem resume nộp vào company mình sở hữu
     * - User thường: chỉ xem resume của mình
     */
    @Operation(summary = "Lấy hồ sơ theo ID", description = "Lấy thông tin chi tiết của hồ sơ theo ID.")
    @GetMapping("/{resumeId}")
    public ResponseEntity<?> getResumeById(@PathVariable Long resumeId) {
        log.info("API get resume by id={}", resumeId);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "API get resume successfully", resumeService.getResumeById(resumeId)));
    }

    /**
     * - Admin: tất cả resumes
     * - Owner: resumes nộp vào company mình sở hữu
     * - User thường: resumes do mình tạo
     */
    @Operation(summary = "Lấy danh sách hồ sơ", description = "Lấy danh sách hồ sơ với phân trang.")
    @GetMapping
    public ResponseEntity<?> getResumes(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        log.info("API get paged resumes, page={}, size={}", page, size);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "API get paged resume successfully", resumeService.getAllResumes(page, size)));
    }
}
