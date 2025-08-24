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

    @PostMapping("/{jobId}")
    public ResponseData<?> saveJob(@PathVariable Long jobId,
                                   @AuthenticationPrincipal CustomUserDetails user) {
        log.info("User {} is saving job {}", user.getId(), jobId);
        try {
            saveJobService.saveJob(user.getId(), jobId);
            return new ResponseData<>(HttpStatus.OK.value(), "Lưu công việc thành công", null);
        } catch (Exception e) {
            log.error("Lỗi khi lưu job: {}", e.getMessage());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Lưu công việc thất bại");
        }
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
