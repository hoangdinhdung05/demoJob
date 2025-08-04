package com.demoJob.demo.controller.User;

import com.demoJob.demo.dto.request.User.Admin.UpdateUserRolesRequest;
import com.demoJob.demo.dto.request.User.Admin.UserRequest;
import com.demoJob.demo.dto.request.User.Admin.UserUpdateRequest;
import com.demoJob.demo.dto.response.User.AdminUserResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.UserService1.AdminUserService;
import com.demoJob.demo.util.UserStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Validated
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * Endpoint để admin tạo người dùng mới
     *
     * @param request thông tin người dùng cần tạo
     * @return ResponseEntity chứa thông tin người dùng đã tạo
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid UserRequest request) {
        log.info("[Admin-User] Admin is creating user with username: {}", request.getUsername());
        AdminUserResponse response = adminUserService.adminCreateUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseData<>(201, "Create user successfully", response));
    }

    /**
     * Endpoint để admin lấy thông tin người dùng theo ID
     *
     * @param userId ID của người dùng cần lấy thông tin
     * @return ResponseEntity chứa thông tin người dùng
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable @Min(1) Long userId) {
        log.info("[Admin-User] Admin is fetching user with ID: {}", userId);
        AdminUserResponse response = adminUserService.adminGetUserByUserId(userId);
        return ResponseEntity.ok(new ResponseData<>(200, "Get user successfully", response));
    }

    /**
     * Endpoint để admin cập nhật thông tin người dùng
     *
     * @param userId  ID của người dùng cần cập nhật
     * @param request thông tin cập nhật
     * @return ResponseEntity chứa thông tin người dùng đã cập nhật
     */
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable @Min(1) Long userId,
                                        @RequestBody @Valid UserUpdateRequest request) {
        log.info("[Admin-User] Admin is updating user with ID: {}", userId);
        AdminUserResponse response = adminUserService.adminUpdateUser(userId, request);
        return ResponseEntity.ok(new ResponseData<>(200, "Update user successfully", response));
    }

    /**
     * Endpoint để admin cập nhật vai trò của người dùng
     *
     * @param userId ID của người dùng cần cập nhật vai trò
     * @param request chứa các vai trò mới
     * @return ResponseEntity chứa thông tin người dùng đã cập nhật vai trò
     */
    @PutMapping("/{userId}/roles")
    public ResponseEntity<?> updateUserRoles(@PathVariable Long userId,
                                             @RequestBody @Valid UpdateUserRolesRequest request) {
        log.info("[Admin-User] Updating roles of user ID: {}", userId);
        AdminUserResponse response = adminUserService.adminUpdateUserRoles(userId, request);
        return ResponseEntity.ok(new ResponseData<>(200, "Update user roles successfully", response));
    }

    /**
     * Endpoint để admin xóa người dùng theo ID
     *
     * @param userId ID của người dùng cần xóa
     * @return ResponseEntity với mã trạng thái 204 No Content
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable @Min(1) Long userId) {
        log.info("[Admin-User] Admin is deleting user with ID: {}", userId);
        adminUserService.adminDeleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint để admin xóa mềm người dùng theo ID
     *
     * @param userId ID của người dùng cần xóa mềm
     * @return ResponseEntity với mã trạng thái 204 No Content
     */
    @PatchMapping("/{userId}/soft-delete")
    public ResponseEntity<?> softDeleteUser(@PathVariable @Min(1) Long userId,
                                            @RequestParam UserStatus status) {
        log.info("[Admin-User] Admin is soft deleting user with ID: {}", userId);
        adminUserService.adminSoftDeleteUser(userId, status);

        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint để admin lấy danh sách người dùng với phân trang
     *
     * @param page số trang (mặc định là 0)
     * @param size kích thước trang (mặc định là 10)
     * @return ResponseEntity chứa danh sách người dùng
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info("[Admin-User] Admin is fetching list of users, page: {}, size: {}", page, size);
        PageResponse<?> response = adminUserService.adminGetListUser(page, size);
        return ResponseEntity.ok(new ResponseData<>(200, "Get list users successfully", response));
    }

}
