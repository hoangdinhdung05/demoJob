package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.UserDTO;
import com.demoJob.demo.dto.request.Admin.Users.UserCompanyRequest;
import com.demoJob.demo.dto.request.Admin.Users.UserProfileRequest;
import com.demoJob.demo.dto.request.Admin.Users.UserRequest;
import com.demoJob.demo.dto.request.Admin.Users.UserUpdateRequest;
import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.dto.response.Admin.User.AdminCreateUserResponse;
import com.demoJob.demo.dto.response.Admin.User.AdminUserProfileResponse;
import com.demoJob.demo.dto.response.Admin.User.RoleUseResponse;
import com.demoJob.demo.dto.response.RegisterResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.*;
import com.demoJob.demo.repository.*;
import com.demoJob.demo.service.UserService;
import com.demoJob.demo.util.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final UserHasRoleRepository userHasRoleRepository;
    private final UserCompanyRepository userCompanyRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public RegisterResponse createUser(RegisterRequest request) {
        // 1. Tìm role mặc định
        Role defaultRole = roleRepository.findByName("user") // hoặc "ROLE_USER"
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        // 2. Tạo user chưa lưu
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(false)
                .build();

        // 3. Tạo liên kết UserHasRole
        UserHasRole userHasRole = UserHasRole.builder()
                .user(user)
                .role(defaultRole)
                .build();

        // 4. Gán set role vào User (để không bị null)
        user.setUserHasRoles(Set.of(userHasRole));

        // 5. Lưu User (sẽ cascade luôn UserHasRole nếu bạn cấu hình đúng)
        user = userRepository.save(user);

        return toResponse(user);
    }

    @Override
    public AdminCreateUserResponse adminCreateUser(UserRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        if (request.getUserProfile() != null) {
            UserProfileRequest profileRequest = request.getUserProfile();
            UserProfile profile = UserProfile.builder()
                    .avatarUrl(profileRequest.getAvatarUrl())
                    .address(profileRequest.getAddress())
                    .phone(profileRequest.getPhone())
                    .gender(profileRequest.getGender())
                    .birthDate(profileRequest.getBirthDate())
                    .user(user)
                    .build();
            user.setUserProfile(profile);
        }

        User newUser = user;
        Set<UserHasRole> userHasRoles = request.getRoles().stream()
                .map(roleName -> {
                    Role role = roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                    return UserHasRole.builder()
                            .user(newUser)
                            .role(role)
                            .build();
                })
                .collect(Collectors.toSet());

        user.setUserHasRoles(userHasRoles);

        //check comapny
        if (request.getCompanyInfo() != null) {
            UserCompanyRequest companyInfo = request.getCompanyInfo();

            Company company = companyRepository.findById(companyInfo.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            UserCompany userCompany = UserCompany.builder()
                    .user(user)
                    .company(company)
                    .position(companyInfo.getPosition())
                    .build();

            user.setUserCompanies(Set.of(userCompany));
        }

        user = userRepository.save(user);

        log.info("Admin create user successfully with userId={}", user.getId());

        return convertAdminCreateUser(user);
    }

    @Override
    public AdminCreateUserResponse adminUpdateUser(long userId, UserUpdateRequest request) {
        // Check userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getUserProfile() != null) {
            UserProfile profile = getUserProfile(request, user);
            user.setUserProfile(profile);
        }

        // Update roles safely for Hibernate

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            User finalUser = user;
            Set<UserHasRole> newRoles = request.getRoles().stream()
                    .map(roleName -> {
                        Role role = roleRepository.findByName(roleName)
                                .orElseThrow(() -> new RuntimeException("Role name not found with name: " + roleName));
                        return UserHasRole.builder()
                                .user(finalUser)
                                .role(role)
                                .build();
                    })
                    .collect(Collectors.toSet());

            user.getUserHasRoles().clear();
            userRepository.save(user); // <-- Bắt buộc phải flush sau khi clear
            user.getUserHasRoles().addAll(newRoles);
        } else {
            user.getUserHasRoles().clear();
            userRepository.save(user); // <-- flush nếu xóa hết role
        }

        //check company
        if (request.getCompanyInfo() != null) {
            updateUserCompany(user, request.getCompanyInfo());
        }

        user = userRepository.save(user);

        log.info("Admin update user with user id={}", userId);

        return convertAdminCreateUser(user);
    }

    private void updateUserCompany(User user, UserCompanyRequest companyInfo) {
        // Xóa quan hệ cũ (nếu dùng orphanRemoval=true thì chỉ cần clear)
        user.getUserCompanies().clear();

        Company company = companyRepository.findById(companyInfo.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        UserCompany userCompany = UserCompany.builder()
                .user(user)
                .company(company)
                .position(companyInfo.getPosition())
                .build();

        user.getUserCompanies().add(userCompany);
    }


    private static UserProfile getUserProfile(UserUpdateRequest request, User user) {
        UserProfileRequest profileRequest = request.getUserProfile();

        UserProfile profile = user.getUserProfile();
        //check user
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
        }

        profile.setAvatarUrl(profileRequest.getAvatarUrl());
        profile.setAddress(profileRequest.getAddress());
        profile.setPhone(profileRequest.getPhone());
        profile.setGender(profileRequest.getGender());
        profile.setBirthDate(profileRequest.getBirthDate());
        return profile;
    }

    @Override
    public AdminCreateUserResponse adminGetUserByUserId(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " +  userId));

        log.info("Admin get user with id={}", userId);

        return convertAdminCreateUser(user);
    }

    @Override
    public PageResponse<?> adminGetListUser(int page, int size) {
        Page<User> userPages = userRepository.findAll(PageRequest.of(page, size));

        List<RegisterResponse> responses = userPages.stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<UserDTO>builder()
                .page(userPages.getNumber() + 1)
                .size(userPages.getSize())
                .total(userPages.getTotalElements())
//                .items(responses)
                .build();
    }

    @Override
    public void adminDeleteUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        log.info("Admin delete User with id={}", userId);

//        userRepository.delete(user);
        user.setStatus(UserStatus.DELETE);
        user = userRepository.save(user);
    }

    @Override
    public AdminCreateUserResponse changeUserStatus(long userId, UserStatus userStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(userStatus);
        user = userRepository.save(user);

        return convertAdminCreateUser(user);
    }


    @Override
    public User findOrCreateUser(String email, String name) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {

                    Role role = roleRepository.findByName("user")
                            .orElseThrow(() -> new RuntimeException("Role name not found"));

                    User user = new User();
                    user.setUsername(name);
                    user.setEmail(email);

                    UserHasRole userHasRole = new UserHasRole();
                    userHasRole.setUser(user);
                    userHasRole.setRole(role);

                    user.setUserHasRoles(Set.of(userHasRole));

                    return userRepository.save(user);
                });
    }


    private UserDTO convertToDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getUserHasRoles().stream()
                        .map(u -> u.getRole().getName())
                        .collect(Collectors.toSet())
                )
                .build();
    }

    private RegisterResponse toResponse(User user) {
        return RegisterResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    private AdminCreateUserResponse convertAdminCreateUser(User user) {
        return AdminCreateUserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .userStatus(user.getStatus())
                .userProfile(
                        Optional.ofNullable(user.getUserProfile())
                                .map(profile -> AdminUserProfileResponse.builder()
                                        .avatarUrl(profile.getAvatarUrl())
                                        .address(profile.getAddress())
                                        .phone(profile.getPhone())
                                        .birthDate(profile.getBirthDate())
                                        .gender(profile.getGender())
                                        .build())
                                .orElse(null)
                )
                .roles(user.getUserHasRoles().stream()
                        .map(urs -> {
                            Role role = urs.getRole();
                            return new RoleUseResponse(role.getId(), role.getName(), role.getCreatedBy(), role.getUpdatedBy());
                        })
                        .collect(Collectors.toSet())
                )
                .build();
    }
}
