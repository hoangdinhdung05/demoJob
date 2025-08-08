package com.demoJob.demo.service.UserService;

import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.entity.Role;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserHasRole;
import com.demoJob.demo.entity.UserProfile;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.repository.RoleRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.util.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCreationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUserEntity(RegisterRequest request, Set<String> roleNames) {
        validateUserUniqueFields(request.getEmail(), request.getUsername());
        Set<Role> roles = getRoles(roleNames);

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
        defaultRoles(user, roles);
        return userRepository.save(user);
    }

    public User createSocialUser(String email, String name, Set<String> roleNames) {
        validateUserUniqueFields(email, null);
        Set<Role> roles = getRoles(roleNames);

        User user = User.builder()
                .username(generateUniqueUsername(name))
                .email(email)
                .firstName(name)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .build();

        defaultProfile(user);
        defaultRoles(user, roles);
        return userRepository.save(user);
    }

    //======//======//

    private Set<Role> getRoles(Set<String> roleNames) {
        Set<Role> roles = roleRepository.findByNameIn(roleNames)
                .orElseThrow(() -> new InvalidDataException("Invalid role names: " + roleNames));
        if (roles.isEmpty()) {
            throw new InvalidDataException("No valid roles found: " + roleNames);
        }
        return roles;
    }

    private void defaultRoles(User user, Set<Role> roles) {
        Set<UserHasRole> userHasRoles = roles.stream()
                .map(role -> UserHasRole.builder().user(user).role(role).build())
                .collect(Collectors.toSet());
        user.setUserHasRoles(userHasRoles);
    }

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
