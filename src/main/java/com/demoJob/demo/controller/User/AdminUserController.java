package com.demoJob.demo.controller.User;

import com.demoJob.demo.dto.request.User.Admin.AdminCreateUserRequest;
import com.demoJob.demo.dto.request.User.Admin.UserAdminUpdateRequest;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.UserService.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_HR')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * Admin tạo mới người dùng (khác với user tự đăng ký).
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid AdminCreateUserRequest request) {
        log.info("API admin create user with username={}", request.getUsername());
        adminUserService.createUser(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Create user successfully"));
    }

    /**
     * Lấy danh sách user (có phân trang).
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("API admin get all users page={} size={}", page, size);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "Get users successfully", adminUserService.getAllUsers(page, size)));
    }

    /**
     * Lấy chi tiết user theo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        log.info("API admin get user detail id={}", id);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "Get user detail successfully", adminUserService.getUserById(id)));
    }

    /**
     * Cập nhật user theo ID.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserAdminUpdateRequest request) {
        log.info("API admin update user id={}", id);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                "Update user successfully", adminUserService.updateUser(id, request)));
    }

    /**
     * Xóa (soft delete) user theo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        log.info("API admin delete user id={}", id);
        adminUserService.deleteUser(id);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Delete user successfully"));
    }
}
