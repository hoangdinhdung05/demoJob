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

        //Validate
        validateUserActionPermission(user, "UPDATE");
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
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));

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

        //Validate
        if (user.getStatus() == UserStatus.DELETE) {
            throw new InvalidDataException("User account is already deleted.");
        }
        validateUserActionPermission(user, "DELETE");

        user.setStatus(UserStatus.DELETE);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User {} marked as deleted", user.getId());
    }

    // ==================== Private Helpers ==================== //

    /**
     * Tìm User thông qua userId
     *
     * @param userId userId cần tìm
     * @return trả ra thông tin User
     */
    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }

    /**
     * Lấy ra role của người dùng
     *
     * @param action hành động
     * @return trả ra role của user
     */
    private User getCurrentUserWithRoles(String action) {
        long userId = SecurityUtils.getCurrentUserId();
        log.info("{} for userId: {}", action, userId);
        return userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new NotFoundException("Current user not found with ID: " + userId));
    }

    /**
     * Validate permission cho các action cơ bản (UPDATE, DELETE)
     * Logic: chỉ cho phép thao tác trên user có level thấp hơn
     */
    private void validateUserActionPermission(User targetUser, String action) {
        User currentUser = getCurrentUserWithRoles("Validating permission: " + action);

        // Không cho phép thao tác trên chính mình
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new InvalidDataException("Bạn không thể " + action.toLowerCase() + " chính mình");
        }

        // Load target user với roles
        User targetUserWithRoles = userRepository.findByIdWithRoles(targetUser.getId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + targetUser.getId()));

        int currentUserLevel = getCurrentUserMaxLevel(currentUser);
        int targetUserLevel = getTargetUserMaxLevel(targetUserWithRoles);

        // Chỉ cho phép thao tác trên user có level thấp hơn
        if (currentUserLevel <= targetUserLevel) {
            throw new InvalidDataException(
                    "Bạn không đủ quyền để " + action.toLowerCase() + " user có quyền cao hơn hoặc bằng quyền của bạn"
            );
        }

        log.info("Permission validated: user {} (level {}) can {} user {} (level {})",
                currentUser.getId(), currentUserLevel, action, targetUser.getId(), targetUserLevel);
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

        // Cập nhật roles (có validate riêng cho roles)
        updateUserRoles(request, user);
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new InvalidDataException("Email already exists: " + email);
        }
    }

    private void updateUserRoles(UserAdminUpdateRequest request, User user) {
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            return; // Không có roles để update
        }

        Set<Role> newRoles = roleRepository.findByNameIn(request.getRoles())
                .orElseThrow(() -> new InvalidDataException("Invalid roles: " + request.getRoles()));

        // Load target user kèm roles để tính level chính xác
        User targetUser = userRepository.findByIdWithRoles(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + user.getId()));

        // Validate đặc biệt cho role update
        validateRoleUpdatePermission(targetUser, newRoles);

        // Clear roles cũ và set roles mới
        targetUser.getUserHasRoles().clear();
        newRoles.forEach(role -> targetUser.getUserHasRoles().add(new UserHasRole(targetUser, role)));

        userRepository.save(targetUser);
    }

    /**
     * Validate đặc biệt cho role update - logic ok hơn
     * Logic: chỉ được gán role có level thấp hơn
     */
    private void validateRoleUpdatePermission(User targetUser, Set<Role> newRoles) {
        User currentUser = getCurrentUserWithRoles("Update roles");

        // Không cho phép tự update role của mình
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new InvalidDataException("Bạn không thể thay đổi role của chính mình");
        }

        int currentUserLevel = getCurrentUserMaxLevel(currentUser);
        int targetCurrentLevel = getTargetUserMaxLevel(targetUser);
        int newRolesMaxLevel = getNewRolesMaxLevel(newRoles);

        // Chỉ được thay đổi role của user có level thấp hơn
        if (currentUserLevel <= targetCurrentLevel) {
            throw new InvalidDataException(
                    "Bạn không đủ quyền để thay đổi role của user có quyền cao hơn hoặc bằng quyền của bạn"
            );
        }

        // Chỉ được gán role có level thấp hơn mình
        if (currentUserLevel <= newRolesMaxLevel) {
            throw new InvalidDataException(
                    "Bạn không đủ quyền để gán role cao hơn hoặc bằng quyền của mình"
            );
        }

        log.info("Role update permission validated: user {} (level {}) can assign roles (max level {}) to user {} (current level {})",
                currentUser.getId(), currentUserLevel, newRolesMaxLevel, targetUser.getId(), targetCurrentLevel);
    }

    private int getCurrentUserMaxLevel(User currentUser) {
        return currentUser.getUserHasRoles().stream()
                .mapToInt(r -> RoleLevel.fromRoleName(r.getRole().getName()).getLevel())
                .max()
                .orElse(0);
    }

    private int getTargetUserMaxLevel(User targetUser) {
        return targetUser.getUserHasRoles().stream()
                .mapToInt(r -> RoleLevel.fromRoleName(r.getRole().getName()).getLevel())
                .max()
                .orElse(0);
    }

    private int getNewRolesMaxLevel(Set<Role> newRoles) {
        return newRoles.stream()
                .mapToInt(r -> RoleLevel.fromRoleName(r.getName()).getLevel())
                .max()
                .orElse(0);
    }
}