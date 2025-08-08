package com.demoJob.demo.controller.User;

import com.demoJob.demo.dto.request.User.Client.UserAccountUpdateRequest;
import com.demoJob.demo.dto.request.User.Client.UserProfileUpdateRequest;
import com.demoJob.demo.dto.request.User.Client.ChangePasswordRequest;
import com.demoJob.demo.dto.response.User.UserInfoResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.UserService.UserClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
@Slf4j
@Valid
public class UserClientController {

    private final UserClientService userClientService;

    @GetMapping("/info")
    public ResponseEntity<?> getInfo() {
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User info retrieved successfully", userClientService.getInfo()));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getInfoDetails() {
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User details retrieved successfully", userClientService.getInfoDetails()));
    }

    @PatchMapping("/account")
    public ResponseEntity<?> updateAccountInfo(@RequestBody @Valid UserAccountUpdateRequest request) {
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User account info updated successfully",
                userClientService.updateAccountInfo(request)));
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfileInfo(@RequestBody @Valid UserProfileUpdateRequest request) {
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User profile info updated successfully",
                userClientService.updateProfileInfo(request)));
    }

    @PatchMapping("/")
    public ResponseEntity<?> softDeleteMyAccount() {
        userClientService.softDeleteMyAccount();
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User account deleted successfully", null));
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changeMyPassword(@RequestBody @Valid ChangePasswordRequest request) {
        userClientService.changeMyPassword(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User password changed successfully", null));
    }

    @GetMapping("/{userId}/info")
    public ResponseEntity<?> getUserInfo(@PathVariable Long userId) {
        UserInfoResponse userInfo = userClientService.getPublicInfo(userId);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User info retrieved successfully", userInfo));
    }
}
