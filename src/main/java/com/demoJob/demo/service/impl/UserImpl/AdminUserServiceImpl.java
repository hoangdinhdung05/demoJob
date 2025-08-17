package com.demoJob.demo.service.impl.UserImpl;

import com.demoJob.demo.dto.request.User.Admin.AdminCreateUserRequest;
import com.demoJob.demo.dto.request.User.Admin.UserAdminUpdateRequest;
import com.demoJob.demo.dto.response.User.UserDetailResponse;
import com.demoJob.demo.dto.response.User.UserInfoResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.Role;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserHasRole;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.exception.NotFoundException;
import com.demoJob.demo.mapper.UserMapper;
import com.demoJob.demo.repository.RoleRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.SecurityUtils;
import com.demoJob.demo.service.UserService.AdminUserService;
import com.demoJob.demo.service.UserService.UserFactoryService;
import com.demoJob.demo.util.RoleLevel;
import com.demoJob.demo.util.UserStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import static com.demoJob.demo.mapper.UserMapper.toResponseFullData;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserFactoryService userFactoryService;

    /**
     * Admin tạo mới user (khác với user tự đăng ký).
     */
    @Override
    public void createUser(AdminCreateUserRequest request) {
        log.info("Admin creating user with username={}", request.getUsername());
        userFactoryService.adminCreateUserEntity(request);
    }

    /**
     * Admin cập nhật thông tin user theo ID.
     */
    @Override
    @Transactional
    public UserDetailResponse updateUser(Long userId, UserAdminUpdateRequest request) {
        log.info("Admin updating user with id={}", userId);

        User user = findUserByIdOrThrow(userId);
        validateUpdateRequest(request, user);

        User updatedUser = userRepository.save(user);
        return toResponseFullData(updatedUser);
    }

    /**
     * Admin lấy chi tiết user.
     */
    @Override
    public UserDetailResponse getUserById(Long userId) {
        log.info("Admin fetching user detail by id={}", userId);
        return toResponseFullData(findUserByIdOrThrow(userId));
    }

    /**
     * Admin lấy danh sách user có phân trang.
     */
    @Override
    public PageResponse<UserInfoResponse> getAllUsers(int page, int size) {
        Page<User> userPage = userRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"))
        );

        List<UserInfoResponse> responses = userPage.stream()
                .map(UserMapper::toResponse)
                .toList();

        return PageResponse.<UserInfoResponse>builder()
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .total(userPage.getTotalElements())
                .items(responses)
                .build();
    }

    /**
     * Admin xóa (soft delete) user.
     */
    @Override
    public void deleteUser(Long userId) {
        log.info("Admin deleting user with id={}", userId);
        User user = findUserByIdOrThrow(userId);

        if (user.getStatus() == UserStatus.DELETE) {
            throw new InvalidDataException("User account is already deleted.");
        }

        user.setStatus(UserStatus.DELETE);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User {} marked as deleted", user.getId());
    }

    // ==================== Private Helpers ==================== //

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }

    private User getCurrentUser(String action) {
        long userId = SecurityUtils.getCurrentUserId();
        log.info("{} for userId: {}", action, userId);
        return findUserByIdOrThrow(userId);
    }

    private void validateUpdateRequest(UserAdminUpdateRequest request, User user) {
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName().trim());
        }

        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            validateUniqueEmail(request.getEmail());
            user.setEmail(request.getEmail().trim());
        }

        if (request.getEmailVerified() != null) {
            user.setEmailVerified(request.getEmailVerified());
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        // Cập nhật roles
        updateUserRoles(request, user);
    }


    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new InvalidDataException("Email already exists: " + email);
        }
    }

    private void updateUserRoles(UserAdminUpdateRequest request, User user) {
        if (request.getRoles() == null || request.getRoles().isEmpty()) return;
        Set<Role> newRoles = roleRepository.findByNameIn(request.getRoles())
                .orElseThrow(() -> new InvalidDataException("Invalid roles: " + request.getRoles()));

        // Load target user kèm roles để tính level chính xác
        User targetUser = userRepository.findByIdWithRoles(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + user.getId()));

        // Validate roles + quyền người update
        validateRoles(request, targetUser, newRoles);

        // Clear roles cũ
        targetUser.getUserHasRoles().clear();

        newRoles.forEach(role -> targetUser.getUserHasRoles().add(new UserHasRole(targetUser, role)));

        userRepository.save(targetUser);
    }


    private void validateRoles(UserAdminUpdateRequest request, User targetUser, Set<Role> newRoles) {
        // Lấy người update
        User currentUser = getCurrentUser("Update roles");

        int updaterLevel = currentUser.getUserHasRoles().stream()
                .mapToInt(r -> RoleLevel.fromRoleName(r.getRole().getName()).getLevel())
                .max()
                .orElse(0);

        int targetCurrentLevel = targetUser.getUserHasRoles().stream()
                .mapToInt(r -> RoleLevel.fromRoleName(r.getRole().getName()).getLevel())
                .max()
                .orElse(0);

        int newRolesLevel = newRoles.stream()
                .mapToInt(r -> RoleLevel.fromRoleName(r.getName()).getLevel())
                .max()
                .orElse(0);

        if (updaterLevel < targetCurrentLevel) {
            throw new InvalidDataException(
                    "Bạn không đủ quyền để thay đổi user có quyền cao hơn hoặc bằng quyền của bạn"
            );
        }

        if (updaterLevel < newRolesLevel) {
            throw new InvalidDataException(
                    "Bạn không đủ quyền để gán role cao hơn quyền của mình"
            );
        }
    }
}
