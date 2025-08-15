package com.demoJob.demo.service.impl.UserImpl;

import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.dto.request.User.Client.ChangePasswordRequest;
import com.demoJob.demo.dto.request.User.Client.UserAccountUpdateRequest;
import com.demoJob.demo.dto.request.User.Client.UserProfileUpdateRequest;
import com.demoJob.demo.dto.response.User.UserDetailResponse;
import com.demoJob.demo.dto.response.User.UserInfoResponse;
import com.demoJob.demo.dto.response.User.UserUpdateResponse;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.SecurityUtils;
import com.demoJob.demo.service.UserService.UserClientService;
import com.demoJob.demo.service.UserService.UserCreationService;
import com.demoJob.demo.util.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Set;
import static com.demoJob.demo.mapper.UserMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserClientServiceImpl implements UserClientService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCreationService userCreationService;

    @Override
    public void createUser(RegisterRequest request) {
        userCreationService.createUserEntity(request, Set.of("user"));
    }

    @Override
    public User loadOrCreateOAuth2User(String email, String name) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userCreationService.createSocialUser(email, name, Set.of("user")));
    }

    @Override
    public UserInfoResponse getInfo() {
        return toResponse(getCurrentActiveUser("Fetching basic info"));
    }

    @Override
    public UserDetailResponse getInfoDetails() {
        return toResponseFullData(getCurrentActiveUser("Fetching full info"));
    }

    @Override
    public UserUpdateResponse updateCurrentUserAccountInfo(UserAccountUpdateRequest request) {
        User user = getCurrentActiveUser("Updating account info");

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getEmail() != null) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), user.getId())) {
                throw new InvalidDataException("Email is already in use.");
            }
            user.setEmail(request.getEmail());
        }

        return toResponseUpdate(userRepository.save(user));
    }

    @Override
    public UserUpdateResponse updateCurrentUserProfileInfo(UserProfileUpdateRequest request) {
        User user = getCurrentActiveUser("Updating profile info");

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

        return toResponseUpdate(userRepository.save(user));
    }

    @Override
    public void deactivateMyAccount() {
        User user = getCurrentActiveUser("Soft deleting account");
        if (user.getStatus() == UserStatus.DELETE) {
            throw new InvalidDataException("User account is already deleted.");
        }
        user.setStatus(UserStatus.DELETE);
        user.setDeletedAt(LocalDateTime.now());

        userRepository.save(user);

        log.info("User {} marked as deleted", user.getId());
    }

    @Override
    public void changeMyPassword(ChangePasswordRequest request) {
        User user = getCurrentActiveUser("Changing password");

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidDataException("Current password is incorrect.");
        }
        if (request.getNewPassword().matches(request.getCurrentPassword())) {
            throw new InvalidDataException("New password must be different from the current password.");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("New password and confirm password do not match.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password updated for user {}", user.getId());
    }

    @Override
    public UserInfoResponse getPublicInfo(Long userId) {
        return toResponse(getActiveUserById(userId));
    }

    //================ Private Helpers ================//

    private User getCurrentActiveUser(String action) {
        long userId = SecurityUtils.getCurrentUserId();
        log.info("{} for userId: {}", action, userId);
        return getActiveUserById(userId);
    }

    private User getActiveUserById(long userId) {
        return userRepository.findByIdAndStatusNot(userId, UserStatus.DELETE)
                .orElseThrow(() -> new InvalidDataException(
                        String.format("User not found or deleted (ID: %d)", userId)
                ));
    }
}
