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

    /**
     * Lấy thông tin cơ bản của người dùng hiện tại.
     * @return ResponseEntity chứa mã trạng thái và thông tin người dùng.
     */
    @GetMapping("/account")
    public ResponseEntity<?> getInfo() {
        log.info("Fetching user account info");
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "Get basic user information successfully", userClientService.getInfo()));
    }

    /**
     * Lấy thông tin chi tiết của người dùng hiện tại.
     * @return ResponseEntity chứa mã trạng thái và thông tin chi tiết người dùng.
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getInfoDetails() {
        log.info("Fetching user profile details");
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "Get detail user information successfully", userClientService.getInfoDetails()));
    }

    /**
     * Cập nhật thông tin tài khoản người dùng.
     * @param request chứa thông tin cập nhật tài khoản người dùng như tên, email, v.v.
     * @return ResponseEntity chứa mã trạng thái và thông tin cập nhật thành công.
     */
    @PatchMapping("/account")
    public ResponseEntity<?> updateAccountInfo(@RequestBody @Valid UserAccountUpdateRequest request) {
        log.info("Updating user account info: {}", request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User account info updated successfully",
                userClientService.updateCurrentUserAccountInfo(request)));
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng.
     * @param request chứa thông tin cập nhật hồ sơ người dùng như ảnh đại diện, mô tả, v.v.
     * @return ResponseEntity chứa mã trạng thái và thông tin cập nhật thành công.
     */
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfileInfo(@RequestBody @Valid UserProfileUpdateRequest request) {
        log.info("Updating user profile info: {}", request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User profile info updated successfully",
                userClientService.updateCurrentUserProfileInfo(request)));
    }

    /**
     * Xóa tài khoản người dùng hiện tại (soft delete).
     * @return ResponseEntity chứa mã trạng thái và thông báo xóa thành công.
     */
    @DeleteMapping
    public ResponseEntity<?> deactivateMyAccount() {
        log.info("Soft deleting user account");
        userClientService.deactivateMyAccount();
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User account deleted successfully", null));
    }

    /**
     * Thay đổi mật khẩu của người dùng hiện tại.
     * @param request chứa thông tin thay đổi mật khẩu bao gồm mật khẩu hiện tại, mật khẩu mới và xác nhận mật khẩu mới.
     * @return ResponseEntity chứa mã trạng thái và thông báo thay đổi mật khẩu thành công.
     */
    @PatchMapping("/password")
    public ResponseEntity<?> changeMyPassword(@RequestBody @Valid ChangePasswordRequest request) {
        log.info("Changing user password: {}", request);
        userClientService.changeMyPassword(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "User password changed successfully", null));
    }
}
