package com.demoJob.demo.controller.User;

import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.UserService.UserClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
@Slf4j
@Valid
public class UserController {

    private final UserClientService userClientService;

    @GetMapping("/{userId}/info")
    public ResponseEntity<?> getUserInfo(@PathVariable Long userId) {
        log.info("Fetching public info for user with ID: {}", userId);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User info retrieved successfully", userClientService.getPublicInfo(userId)));
    }

}
