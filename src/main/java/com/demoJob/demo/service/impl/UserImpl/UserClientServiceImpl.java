package com.demoJob.demo.service.impl.UserImpl;

import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.dto.request.User.Client.ChangePasswordRequest;
import com.demoJob.demo.dto.request.User.Client.UserUpdateRequest;
import com.demoJob.demo.dto.response.User.UserDetailResponse;
import com.demoJob.demo.dto.response.User.UserInfoResponse;
import com.demoJob.demo.dto.response.User.UserUpdateResponse;
import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserCompany;
import com.demoJob.demo.entity.UserProfile;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.repository.CompanyRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.SecurityUtils;
import com.demoJob.demo.service.UserService.UserClientService;
import com.demoJob.demo.service.UserService.UserFactoryService;
import com.demoJob.demo.util.enums.CompanyStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Set;
import static com.demoJob.demo.mapper.UserMapper.*;
import static com.demoJob.demo.util.enums.UserStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserClientServiceImpl implements UserClientService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFactoryService userCreationService;
    private final CompanyRepository companyRepository;

    /**
     * Tạo tài khoản người dùng mới.
     * Phương thức này được sử dụng khi người dùng đăng ký tài khoản mới.
     *
     * @param request thông tin đăng ký
     */
    @Override
    public void createUser(RegisterRequest request) {
        userCreationService.createUserEntity(request, Set.of("user"));
    }

    /**
     * Tìm kiếm người dùng theo email, nếu không tìm thấy thì tạo mới.
     * @param email email của người dùng
     * @param name tên của người dùng
     * @return thông tin người dùng
     */
    @Override
    public User getOrCreateOAuth2User(String email, String name) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userCreationService.createOAuth2User(email, name, Set.of("user")));
    }

    /**
     * Lấy thông tin cơ bản của người dùng hiện tại.
     * Phương thức này trả về thông tin cơ bản như tên, email, v.v.
     *
     * @return thông tin người dùng
     */
    @Override
    public UserInfoResponse getInfo() {
        return toResponse(getCurrentActiveUser("Fetching basic info"));
    }

    /**
     * Lấy thông tin chi tiết của người dùng hiện tại.
     * Phương thức này trả về thông tin đầy đủ bao gồm cả profile và công ty.
     *
     * @return thông tin chi tiết người dùng
     */
    @Override
    public UserDetailResponse getInfoDetails() {
        return toResponseFullData(getCurrentActiveUser("Fetching full info"));
    }

    /**
     * User cập nhật thông tin chung của chính mình.
     *
     * @param request thông tin cập nhật
     * @return thông tin cập nhật sau khi thực hiện
     */
    @Override
    public UserUpdateResponse updateCurrentUserInfo(UserUpdateRequest request) {
        User user = getCurrentActiveUser("Updating user info");

        updateBasicInfo(user, request);
        updateCompanyInfo(user, request);

        return toResponseUpdate(userRepository.save(user));
    }

    /**
     * User thay đổi mật khẩu của chính mình.
     *
     * @param request thông tin thay đổi mật khẩu
     */
    @Override
    public void changeMyPassword(ChangePasswordRequest request) {
        User user = getCurrentActiveUser("Changing password");

        validatePassword(request, user);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password updated for user {}", user.getId());
    }

    /**
     * Lấy thông tin công khai của người dùng theo ID.
     * Sử dụng cho việc hiển thị thông tin người dùng công khai.
     *
     * @param userId ID của người dùng
     * @return thông tin công khai của người dùng
     */
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
        return userRepository.findByIdAndStatusNot(userId, DELETE)
                .orElseThrow(() -> new InvalidDataException(
                        String.format("User not found or deleted (ID: %d)", userId)
                ));
    }

    private void validatePassword(ChangePasswordRequest request, User user) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidDataException("Current password is incorrect.");
        }
        if (request.getNewPassword().matches(request.getCurrentPassword())) {
            throw new InvalidDataException("New password must be different from the current password.");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("New password and confirm password do not match.");
        }
    }

    private void updateBasicInfo(User user, UserUpdateRequest request) {
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());

        if (request.getEmail() != null) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), user.getId())) {
                throw new InvalidDataException("Email is already in use.");
            }
            user.setEmail(request.getEmail());
        }

        UserProfile profile = user.getUserProfile();

        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone().isBlank() ? null : request.getPhone());
        }
        if (request.getAvatarUrl() != null) {
            profile.setAvatarUrl(request.getAvatarUrl().isBlank() ? null : request.getAvatarUrl());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress().isBlank() ? null : request.getAddress());
        }
        profile.setGender(request.getGender());
        profile.setBirthDate(request.getBirthDate());
        if (request.getWebsite() != null) {
            profile.setWebsite(request.getWebsite().isBlank() ? null : request.getWebsite());
        }
    }

    private void updateCompanyInfo(User user, UserUpdateRequest request) {
        UserCompany currentCompany = getCurrentCompany(user);

        // 1. Nếu request không có companyId → kết thúc công ty hiện tại
        if (request.getCompanyId() == null) {
            endCurrentCompany(currentCompany);
            return; // không cần xử lý thêm công ty mới
        }

        // 2. Xử lý thêm hoặc đổi công ty mới
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new InvalidDataException("Company not found."));

        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new InvalidDataException("Company is not active.");
        }

        // Nếu công ty mới khác công ty hiện tại → kết thúc công ty cũ và thêm công ty mới
        if (currentCompany == null || !currentCompany.getCompany().getId().equals(company.getId())) {
            endCurrentCompany(currentCompany);
            user.getUserCompanies().add(createUserCompany(user, company));
        }
    }

    private void endCurrentCompany(UserCompany currentCompany) {
        if (currentCompany != null) {
            currentCompany.setEndDate(LocalDate.now());
        }
    }

    private UserCompany getCurrentCompany(User user) {
        return user.getUserCompanies().stream()
                .filter(uc -> uc.getEndDate() == null)
                .findFirst()
                .orElse(null);
    }

    private UserCompany createUserCompany(User user, Company company) {
        return UserCompany.builder()
                .user(user)
                .company(company)
                .startDate(LocalDate.now())
                .build();
    }
}
