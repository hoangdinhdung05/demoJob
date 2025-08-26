package com.demoJob.demo.controller;

import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.security.CustomUserDetails;
import com.demoJob.demo.service.SaveJobService;
import com.demoJob.demo.util.enums.SaveJobStatus;
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
                                     @AuthenticationPrincipal CustomUserDetails user,
                                     @RequestParam SaveJobStatus status) {
        log.info("API user save job or update status with userId={} and jobId={}", user.getId(), jobId);
        saveJobService.saveJob(user.getId(), jobId, status);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseData<>(HttpStatus.CREATED.value(), "Job saved or updated successfully"));
    }

    /**
     * Lấy ra list job mà User đã lưu
     */
    @GetMapping
    public ResponseEntity<?> getAllSaveJobs(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info("API get list save job");
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "API get list save job successfully",
                saveJobService.getAllSavedJobs(page, size)));
    }

}
