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

    /**
     * User xóa Job khỏi danh sách yêu thích
     */
    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> deleteSaveJob(@PathVariable Long jobId) {
        log.info("API user delete save job {}", jobId);
        saveJobService.deleteSaveJob(jobId);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.NO_CONTENT.value(),
                "API user delete save job successfully"));
    }
}
