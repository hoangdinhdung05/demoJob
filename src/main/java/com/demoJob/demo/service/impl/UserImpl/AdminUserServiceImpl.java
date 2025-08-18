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
import java.util.stream.Collectors;
import static com.demoJob.demo.mapper.UserMapper.toResponseFullData;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserFactoryService userFactoryService;

    /**
     * Admin tạo mới user.
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

        User targetUser = findUserByIdWithRolesOrThrow(userId);
        validateUserPermission(targetUser);

        validateRequest(request, targetUser);
        userRepository.save(targetUser);

        return toResponseFullData(targetUser);
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
     * Admin soft-delete user.
     */
    @Override
    public void deleteUser(Long userId) {
        log.info("Admin deleting user with id={}", userId);

        User targetUser = findUserByIdWithRolesOrThrow(userId);
        validateUserPermission(targetUser);

        if (targetUser.getStatus() == UserStatus.DELETE) {
            throw new InvalidDataException("User account is already deleted.");
        }

        targetUser.setStatus(UserStatus.DELETE);
        targetUser.setDeletedAt(LocalDateTime.now());
        userRepository.save(targetUser);

        log.info("User {} marked as deleted", targetUser.getId());
    }

    // ==================== Private Helpers ==================== //

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }

    private User findUserByIdWithRolesOrThrow(Long userId) {
        return userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }

    private User getCurrentUserWithRoles() {
        long currentUserId = SecurityUtils.getCurrentUserId();
        return userRepository.findByIdWithRoles(currentUserId)
                .orElseThrow(() -> new NotFoundException("Current user not found with ID: " + currentUserId));
    }

    private void validateRequest(UserAdminUpdateRequest request, User user) {
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

        updateUserRoles(request, user);
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new InvalidDataException("Email already exists: " + email);
        }
    }

    /**
     * Validate quyền cơ bản: chỉ cho thao tác trên user có level thấp hơn.
     */
    private void validateUserPermission(User targetUser) {
        User currentUser = getCurrentUserWithRoles();

        validateNotSelf(currentUser, targetUser);

        int currentUserLevel = getUserMaxLevel(currentUser);
        int targetUserLevel = getUserMaxLevel(targetUser);

        if (currentUserLevel <= targetUserLevel) {
            throw new InvalidDataException("Bạn không đủ quyền để thao tác với user có quyền cao hơn hoặc bằng quyền của bạn");
        }
    }

    /**
     * Update role mới sau khi validate
     * @param request Thông tin request
     * @param user user được thay đổi
     */
    private void updateUserRoles(UserAdminUpdateRequest request, User user) {
        if (request.getRoles() == null || request.getRoles().isEmpty()) return;

        Set<Role> newRoles = roleRepository.findByNameIn(request.getRoles())
                .orElseThrow(() -> new InvalidDataException("Invalid roles: " + request.getRoles()));

        validateRoleUpdatePermission(user, newRoles);

        user.getUserHasRoles().clear();
        newRoles.forEach(role -> user.getUserHasRoles().add(new UserHasRole(user, role)));
    }

    /**
     * Check role mà thay đổi xem có phù hợp không
     *
     * @param targetUser user được thay đổi
     * @param newRoles Roles mới
     */
    private void validateRoleUpdatePermission(User targetUser, Set<Role> newRoles) {
        User currentUser = getCurrentUserWithRoles();
        validateNotSelf(currentUser, targetUser);

        int currentUserLevel = getUserMaxLevel(currentUser);
        int targetCurrentLevel = getUserMaxLevel(targetUser);
        int newRolesMaxLevel = getMaxLevel(newRoles);

        if (currentUserLevel <= targetCurrentLevel) {
            throw new InvalidDataException("Bạn không đủ quyền để thay đổi role của user có quyền cao hơn hoặc bằng quyền của bạn");
        }

        if (currentUserLevel < newRolesMaxLevel) {
            throw new InvalidDataException("Bạn không đủ quyền để gán role cao quyền của mình");
        }

        log.info("Role update validated: user {} (level {}) can assign roles (max level {}) to user {} (current level {})",
                currentUser.getId(), currentUserLevel, newRolesMaxLevel, targetUser.getId(), targetCurrentLevel);
    }

    /**
     * Không thể thao tác với mình
     * @param currentUser user đang thao tác
     * @param targetUser user được thao tác
     */
    private void validateNotSelf(User currentUser, User targetUser) {
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new InvalidDataException("Bạn không thể thao tác với chính mình");
        }
    }

    /**
     * Trả ra role cao nhất
     * @param roles List role
     * @return trả role cao nhất
     */
    private int getMaxLevel(Collection<Role> roles) {
        return roles.stream()
                .mapToInt(r -> RoleLevel.fromRoleName(r.getName()).getLevel())
                .max().orElse(0);
    }

    /**
     * Trả về level cao nhất của user
     * @param user user cần tìm
     * @return trả ra level
     */
    private int getUserMaxLevel(User user) {
        return getMaxLevel(
                user.getUserHasRoles().stream()
                        .map(UserHasRole::getRole)
                        .collect(Collectors.toSet())
        );
    }
}
