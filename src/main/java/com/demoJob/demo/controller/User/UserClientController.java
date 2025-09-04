package com.demoJob.demo.controller.User;

import com.demoJob.demo.dto.request.User.Client.UserUpdateRequest;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.UserService.UserClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "USER-CLIENT", description = "Quản lý người dùng - Client")
public class UserClientController {

    private final UserClientService userClientService;

    /**
     * Lấy thông tin cơ bản của người dùng hiện tại.
     * @return ResponseEntity chứa mã trạng thái và thông tin người dùng.
     */
    @Operation(summary = "Lấy thông tin cơ bản của người dùng hiện tại", description = "Lấy thông tin cơ bản của người dùng hiện tại.")
    @GetMapping("/info")
    public ResponseEntity<?> getInfo() {
        log.info("Fetching user account info");
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "Get basic user information successfully", userClientService.getInfo()));
    }

    /**
     * Lấy thông tin chi tiết của người dùng hiện tại.
     * @return ResponseEntity chứa mã trạng thái và thông tin chi tiết người dùng.
     */
    @Operation(summary = "Lấy thông tin chi tiết của người dùng hiện tại", description = "Lấy thông tin chi tiết của người dùng hiện tại.")
    @GetMapping("/details")
    public ResponseEntity<?> getInfoDetails() {
        log.info("Fetching user profile details");
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "Get detail user information successfully", userClientService.getInfoDetails()));
    }

    /**
     * Cập nhật một phần thông tin tài khoản người dùng hiện tại.
     * @param request thông tin cập nhật (chỉ gửi field cần thay đổi).
     * @return ResponseEntity chứa mã trạng thái và thông tin cập nhật.
     */
    @Operation(summary = "Cập nhật một phần thông tin tài khoản người dùng hiện tại", description = "Cập nhật một phần thông tin tài khoản người dùng hiện tại.")
    @PutMapping("/account")
    public ResponseEntity<?> updateAccount(@Valid @RequestBody UserUpdateRequest request) {
        log.info("Partially updating current user account information");
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User account updated successfully", userClientService.updateCurrentUserInfo(request)));
    }
}
