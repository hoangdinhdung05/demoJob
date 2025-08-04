package com.demoJob.demo.service.impl.UserImpl;

import com.demoJob.demo.dto.request.User.Client.ChangePasswordRequest;
import com.demoJob.demo.dto.request.User.Client.UserUpdateRequest;
import com.demoJob.demo.dto.response.User.UserBasicInfoResponse;
import com.demoJob.demo.dto.response.User.UserFullInfoResponse;
import com.demoJob.demo.dto.response.User.UserUpdateResponse;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.SecurityUtils;
import com.demoJob.demo.service.UserService1.UserClientService;
import com.demoJob.demo.util.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.demoJob.demo.mapper.UserMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserClientServiceImpl implements UserClientService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * User lấy thông tin cơ bản của chính mình.
     *
     * @return thông tin cơ bản của người dùng
     */
    @Override
    public UserBasicInfoResponse getMyBasicInfo() {

        long userId = SecurityUtils.getCurrentUserId();
        log.info("Fetching basic info for user with ID: {}", userId);

        // Tìm kiếm người dùng theo ID
        User user = findUserById(userId);
        log.info("User found: {}", user);
        return toResponse(user);
    }

    /**
     * User lấy thông tin đầy đủ của chính mình.
     *
     * @return thông tin đầy đủ của người dùng
     */
    @Override
    public UserFullInfoResponse getMyFullInfo() {
        long userId = SecurityUtils.getCurrentUserId();
        log.info("Fetching full info for user with ID: {}", userId);
        // Tìm kiếm người dùng theo ID
        User user = findUserById(userId);
        log.info("User found: {}", user);
        return toResponseFullData(user);
    }

    /**
     * User cập nhật thông tin cá nhân của chính mình.
     *
     * @param request thông tin cập nhật
     * @return thông tin cập nhật sau khi thực hiện
     */
    @Override
    public UserUpdateResponse updateMyInfo(UserUpdateRequest request) {
        long userId = SecurityUtils.getCurrentUserId();
        log.info("Updating info for user with ID: {}", userId);
        // Tìm kiếm người dùng theo ID
        User user = findUserById(userId);
        log.info("User found: {}", user);
        // Cập nhật thông tin người dùng
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.getUserProfile().setPhone(request.getPhone());
        }
        if (request.getAvatarUrl() != null) {
            user.getUserProfile().setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getAddress() != null) {
            user.getUserProfile().setAddress(request.getAddress());
        }
        if (request.getGender() != null) {
            user.getUserProfile().setGender(request.getGender());
        }
        if (request.getBirthDate() != null) {
            user.getUserProfile().setBirthDate(request.getBirthDate());
        }
        if (request.getWebsite() != null) {
            user.getUserProfile().setWebsite(request.getWebsite());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getUsername());

        return toResponseUpdate(updatedUser);
    }

    /**
     * User xóa tài khoản của chính mình.
     * Tài khoản sẽ được đánh dấu là đã xóa (soft delete).
     */
    @Override
    public void softDeleteMyAccount() {
        long userId = SecurityUtils.getCurrentUserId();
        log.info("Soft deleting user with ID: {}", userId);

        User user = findUserById(userId);

        user.setStatus(UserStatus.DELETE);
        userRepository.save(user);

        log.info("User marked as deleted successfully: {}", userId);
    }

    /**
     * User thay đổi mật khẩu của chính mình.
     *
     * @param request thông tin thay đổi mật khẩu
     */
    @Override
    public void changeMyPassword(ChangePasswordRequest request) {
        long userId = SecurityUtils.getCurrentUserId();
        log.info("Changing password for user with ID: {}", userId);

        User user = findUserById(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password updated successfully for user: {}", user.getUsername());
    }

    //=====//=====//
    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }
}
