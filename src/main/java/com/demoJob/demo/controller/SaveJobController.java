package com.demoJob.demo.controller;

import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.dto.response.system.ResponseError;
import com.demoJob.demo.security.CustomUserDetails;
import com.demoJob.demo.service.SaveJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/save-job")
@RequiredArgsConstructor
@Slf4j
public class SaveJobController {

    private final SaveJobService saveJobService;

    /**
     * User lưu lại các Job mà mình quan tâm hoặc yêu thích
     */
    @PostMapping("/{jobId}")
    public ResponseEntity<?> saveJob(@PathVariable Long jobId,
                                     @AuthenticationPrincipal CustomUserDetails user) {
        log.info("API user save job with userId={} and jobId={}", user.getId(), jobId);
        saveJobService.saveJob(user.getId(), jobId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseData<>(HttpStatus.CREATED.value(), "Job saved successfully"));
    }

    @DeleteMapping("/{jobId}")
    public ResponseData<?> deleteSaveJob(@PathVariable Long jobId,
                                         @AuthenticationPrincipal CustomUserDetails user) {
        log.info("User {} is deleting saved job {}", user.getId(), jobId);
        try {
            saveJobService.deleteSaveJob(user.getId(), jobId);
            return new ResponseData<>(HttpStatus.OK.value(), "Đã bỏ lưu công việc", null);
        } catch (Exception e) {
            log.error("Lỗi khi bỏ lưu job: {}", e.getMessage());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Bỏ lưu công việc thất bại");
        }
    }
}
