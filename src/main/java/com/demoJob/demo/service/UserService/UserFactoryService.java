package com.demoJob.demo.service.UserService;

import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.entity.Role;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserProfile;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.util.UserRoleUtil;
import com.demoJob.demo.util.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserFactoryService {

    private final UserRoleUtil userRoleUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Tạo người dùng mới từ yêu cầu đăng ký.
     * Phương thức này được sử dụng khi người dùng đăng ký tài khoản mới.
     *
     * @param request thông tin đăng ký
     * @param roleNames tập hợp tên vai trò để gán cho người dùng
     */
    public void createUserEntity(RegisterRequest request, Set<String> roleNames) {
        validateUserUniqueFields(request.getEmail(), request.getUsername());
        Set<Role> roles = userRoleUtil.getRoles(roleNames);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .build();

        defaultProfile(user);
        userRoleUtil.assignRolesToUser(user, roles);
        userRepository.save(user);
    }

    /**
     * Tạo người dùng mới từ thông tin đăng nhập OAuth2.
     * Phương thức này được sử dụng khi người dùng đăng nhập qua mạng xã hội.
     *
     * @param email email của người dùng
     * @param name tên của người dùng
     * @param roleNames tập hợp tên vai trò để gán cho người dùng
     * @return đối tượng User đã được tạo mới
     */
    public User createOAuth2User(String email, String name, Set<String> roleNames) {
        validateUserUniqueFields(email, null);
        Set<Role> roles = userRoleUtil.getRoles(roleNames);

        User user = User.builder()
                .username(generateUniqueUsername(name))
                .email(email)
                .firstName(name)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .build();

        defaultProfile(user);
        userRoleUtil.assignRolesToUser(user, roles);
        return userRepository.save(user);
    }

    //======//======//

    private void defaultProfile(User user) {
        user.setUserProfile(UserProfile.builder().user(user).build());
    }

    private void validateUserUniqueFields(String email, String username) {
        if (username != null && userRepository.existsByUsername(username)) {
            throw new InvalidDataException("Username already exists: " + username);
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new InvalidDataException("Email already exists: " + email);
        }
    }

    private String generateUniqueUsername(String baseName) {
        String username = baseName.trim().replaceAll("\\s+", "_").toLowerCase();
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = username + "_" + counter++;
        }
        return username;
    }
}
