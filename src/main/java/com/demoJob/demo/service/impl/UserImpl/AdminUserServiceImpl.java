package com.demoJob.demo.service.impl.UserImpl;

import com.demoJob.demo.dto.request.User.Admin.UpdateUserRolesRequest;
import com.demoJob.demo.dto.request.User.Admin.UserRequest;
import com.demoJob.demo.dto.request.User.Admin.UserUpdateRequest;
import com.demoJob.demo.dto.response.User.AdminUserResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.Role;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserHasRole;
import com.demoJob.demo.entity.UserProfile;
import com.demoJob.demo.exception.DuplicateResourceException;
import com.demoJob.demo.exception.ResourceNotFoundException;
import com.demoJob.demo.mapper.AdminUserMapper;
import com.demoJob.demo.repository.RoleRepository;
import com.demoJob.demo.repository.UserProfileRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.service.UserService1.AdminUserService;
import com.demoJob.demo.util.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static com.demoJob.demo.mapper.AdminUserMapper.toResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    /**
     * Các repository cần thiết để tương tác với cơ sở dữ liệu
     */
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Tạo người dùng mới
     *
     * @param request Admin nhập thông tin người dùng
     * @return AdminUserResponse chứa thông tin người dùng đã tạo
     */
    @Override
    public AdminUserResponse adminCreateUser(UserRequest request) {
        log.info("Creating user with username: {}", request.getUsername());

        validateUniqueUsernameAndEmail(request.getUsername(), request.getEmail());

        Set<Role> roles = findRolesByNames(request.getRoles());

        User user = buildUserEntity(request);
        Set<UserHasRole> userHasRoles = mapToUserHasRoles(user, roles);
        user.setUserHasRoles(userHasRoles);

        User savedUser = userRepository.save(user);

        UserProfile userProfile = buildUserProfile(request, savedUser);
        userProfileRepository.save(userProfile);

        savedUser.setUserProfile(userProfile);

        log.info("User created successfully: {}", savedUser.getUsername());
        return toResponse(savedUser);
    }


    /**
     * Lấy thông tin người dùng theo ID
     *
     * @param userId ID của người dùng
     * @return AdminUserResponse chứa thông tin người dùng
     */
    @Override
    public AdminUserResponse adminGetUserByUserId(Long userId) {
        log.info("Fetching user with ID: {}", userId);
        return toResponse(findUserById(userId));
    }

    /**
     * Cập nhật thông tin người dùng
     *
     * @param userId  ID của người dùng cần cập nhật
     * @param request Thông tin cập nhật
     * @return AdminUserResponse chứa thông tin người dùng đã cập nhật
     */
    @Override
    public AdminUserResponse adminUpdateUser(Long userId, UserUpdateRequest request) {
        log.info("Updating user with ID: {}", userId);
        User user = findUserById(userId);

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        // Đảm bảo userProfile luôn tồn tại
        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
        }

        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone());
        }

        user.setUserProfile(profile);
        userProfileRepository.save(profile);
        User updatedUser = userRepository.save(user);

        log.info("User updated successfully: {}", updatedUser.getUsername());
        return toResponse(updatedUser);
    }

    /**
     * Cập nhật vai trò của người dùng
     *
     * @param userId ID của người dùng cần cập nhật vai trò
     * @param rolesRequest  Tập hợp các vai trò mới
     * @return AdminUserResponse chứa thông tin người dùng đã cập nhật vai trò
     */
    @Override
    public AdminUserResponse adminUpdateUserRoles(Long userId, UpdateUserRolesRequest rolesRequest) {
        log.info("Updating roles for user with ID: {}", userId);
        User user = findUserById(userId);

        Set<Role> newRoles = findRolesByNames(rolesRequest.getRoles());
        Set<UserHasRole> newUserHasRoles = mapToUserHasRoles(user, newRoles);

        user.getUserHasRoles().clear();
        user.getUserHasRoles().addAll(newUserHasRoles);

        User updatedUser = userRepository.save(user);
        log.info("User roles updated successfully for user ID: {}", userId);

        return toResponse(updatedUser);
    }

    /**
     * Xóa người dùng theo ID
     *
     * @param userId ID của người dùng cần xóa
     */
    @Override
    public void adminDeleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);

        User user = findUserById(userId);
        //Delete user profile if exists
        if (user.getUserProfile() != null) {
            userProfileRepository.delete(user.getUserProfile());
        }
        //Delete user
        userRepository.delete(user);
    }

    /**
     * Xóa mềm người dùng theo ID
     *
     * @param userId ID của người dùng cần xóa mềm
     * @param status Trạng thái mới của người dùng
     */
    @Override
    public void adminSoftDeleteUser(Long userId, UserStatus status) {
        log.info("Soft deleting user with ID: {}", userId);
        User user = findUserById(userId);
        user.setStatus(status);
        userRepository.save(user);
        log.info("User soft deleted successfully, new status: {}", status);
    }

    /**
     * Lấy danh sách người dùng với phân trang
     *
     * @param page Số trang
     * @param size Kích thước trang
     * @return PageResponse chứa danh sách người dùng
     */
    @Override
    public PageResponse<?> adminGetListUser(int page, int size) {
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
        log.info("Fetching user list, page: {}, size: {}", page, size);

        List<AdminUserResponse> adminUserResponseList = userPage.getContent().stream()
                .map(AdminUserMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<AdminUserResponse>builder()
                .page(userPage.getNumber() + 1) // Convert to 1-based index
                .size(userPage.getSize())
                .total(userPage.getTotalElements())
                .items(adminUserResponseList)
                .build();
    }

    //=====//=====//

    /**
     * Tìm người dùng theo ID
     * @param userId
     * @return
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Tìm vai trò theo tên
     * @param roleName
     * @return
     */
    private Role findRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
    }

    /**
     * Tìm các vai trò theo tên
     * @param roleNames
     * @return
     */
    private Set<Role> findRolesByNames(Set<String> roleNames) {
        return roleNames.stream()
                .map(this::findRoleByName)
                .collect(Collectors.toSet());
    }

    /**
     * Kiểm tra tính duy nhất của tên người dùng và email
     * @param username
     * @param email
     */
    private void validateUniqueUsernameAndEmail(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already exists");
        }
    }

    /**
     * Tạo entity User từ request và danh sách role
     */
    private User buildUserEntity(UserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .build();
    }

    /**
     * Xây dựng UserProfile từ request và user
     * @param request
     * @param user
     * @return
     */
    private UserProfile buildUserProfile(UserRequest request, User user) {
        return UserProfile.builder()
                .user(user)
                .phone(request.getPhone())
                .build();
    }

    /**
     * Chuyển đổi danh sách Role thành tập hợp UserHasRole
     * @param user
     * @param roles
     * @return
     */
    private Set<UserHasRole> mapToUserHasRoles(User user, Set<Role> roles) {
        return roles.stream()
                .map(role -> new UserHasRole(user, role))
                .collect(Collectors.toSet());
    }
}
