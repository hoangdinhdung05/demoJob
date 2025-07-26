package base_webSocket_demo.controller;

import base_webSocket_demo.dto.response.system.ResponseData;
import base_webSocket_demo.dto.response.system.ResponseError;
import base_webSocket_demo.security.CustomUserDetails;
import base_webSocket_demo.service.SaveJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/save-job")
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
