package com.demoJob.demo.controller.User;

import com.demoJob.demo.dto.request.User.Client.ChangePasswordRequest;
import com.demoJob.demo.dto.request.User.Client.UserUpdateRequest;
import com.demoJob.demo.dto.response.User.UserBasicInfoResponse;
import com.demoJob.demo.dto.response.User.UserFullInfoResponse;
import com.demoJob.demo.dto.response.User.UserUpdateResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.UserService1.UserClientService;
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
public class UserClientController {

    private final UserClientService userClientService;

    /**
     * Lấy thông tin cơ bản
     */
    @GetMapping("/basic-info")
    public ResponseEntity<ResponseData<UserBasicInfoResponse>> getMyBasicInfo() {
        log.info("[Client-User] Lấy thông tin cơ bản của người dùng hiện tại");
        UserBasicInfoResponse response = userClientService.getMyBasicInfo();
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Lấy thông tin cơ bản thành công", response));
    }

    /**
     * Lấy thông tin đầy đủ
     */
    @GetMapping("/full-info")
    public ResponseEntity<ResponseData<UserFullInfoResponse>> getMyFullInfo() {
        log.info("[Client-User] Lấy thông tin đầy đủ của người dùng hiện tại");
        UserFullInfoResponse response = userClientService.getMyFullInfo();
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Lấy thông tin đầy đủ thành công", response));
    }

    /**
     * Cập nhật thông tin cá nhân
     */
    @PutMapping("/update")
    public ResponseEntity<ResponseData<UserUpdateResponse>> updateMyInfo(@RequestBody @Valid UserUpdateRequest request) {
        log.info("[Client-User] Cập nhật thông tin người dùng hiện tại");
        UserUpdateResponse response = userClientService.updateMyInfo(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Cập nhật thông tin thành công", response));
    }

    /**
     * Đổi mật khẩu
     */
    @PutMapping("/change-password")
    public ResponseEntity<ResponseData<Void>> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        log.info("[Client-User] Đổi mật khẩu người dùng hiện tại");
        userClientService.changeMyPassword(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Đổi mật khẩu thành công"));
    }

    /**
     * Xóa mềm tài khoản
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseData<Void>> deleteMyAccount() {
        log.info("[Client-User] Đánh dấu tài khoản người dùng hiện tại là đã xóa");
        userClientService.softDeleteMyAccount();
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Tài khoản đã được đánh dấu là đã xóa"));
    }
}
