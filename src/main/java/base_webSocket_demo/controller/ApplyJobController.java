package base_webSocket_demo.controller;

import base_webSocket_demo.security.CustomUserDetails;
import base_webSocket_demo.service.ApplyJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class ApplyJobController {

    private final ApplyJobService applyJobService;

    @PostMapping("/{jobId}/apply")
    public ResponseEntity<?> applyJob(
            @PathVariable Long jobId,
            @RequestParam String cvUrl,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        applyJobService.applyJob(jobId, cvUrl, user.getUser());
        return ResponseEntity.ok("Ứng tuyển thành công");
    }
}