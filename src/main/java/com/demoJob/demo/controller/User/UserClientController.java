package com.demoJob.demo.controller.User;

import com.demoJob.demo.dto.request.User.Client.ChangePasswordRequest;
import com.demoJob.demo.dto.request.User.Client.UserAccountUpdateRequest;
import com.demoJob.demo.dto.request.User.Client.UserProfileUpdateRequest;
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

    @GetMapping("/account")
    public ResponseEntity<?> getInfo() {
        log.info("Fetching user account info");
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "Get basic user information successfully\n", userClientService.getInfo()));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getInfoDetails() {
        log.info("Fetching user profile details");
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "Get detail user information successfully\n", userClientService.getInfoDetails()));
    }

    //2 cái này có thể gộp lại thành 1
    @PatchMapping("/account")
    public ResponseEntity<?> updateAccountInfo(@RequestBody @Valid UserAccountUpdateRequest request) {
        log.info("Updating user account info: {}", request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User account info updated successfully",
                userClientService.updateAccountInfo(request)));
    }
    //2 cái này có thể gộp lại thành 1
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfileInfo(@RequestBody @Valid UserProfileUpdateRequest request) {
        log.info("Updating user profile info: {}", request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User profile info updated successfully",
                userClientService.updateProfileInfo(request)));
    }

    // sau khi xóa tài khoản, người dùng sẽ không thể đăng nhập lại được nữa
    //em đang có ý định là call lại api logout để xóa token của người dùng
    @DeleteMapping
    public ResponseEntity<?> softDeleteMyAccount() {
        log.info("Soft deleting user account");
        userClientService.softDeleteMyAccount();
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User account deleted successfully", null));
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changeMyPassword(@RequestBody @Valid ChangePasswordRequest request) {
        log.info("Changing user password: {}", request);
        userClientService.changeMyPassword(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User password changed successfully", null));
    }
}
