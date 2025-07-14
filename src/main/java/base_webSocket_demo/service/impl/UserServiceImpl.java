package base_webSocket_demo.service.impl;

import base_webSocket_demo.dto.UserDTO;
import base_webSocket_demo.dto.request.RegisterRequest;
import base_webSocket_demo.entity.Role;
import base_webSocket_demo.entity.User;
import base_webSocket_demo.entity.UserHasRole;
import base_webSocket_demo.repository.RoleRepository;
import base_webSocket_demo.repository.UserHasRoleRepository;
import base_webSocket_demo.repository.UserRepository;
import base_webSocket_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserHasRoleRepository userHasRoleRepository;
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
    public void createUser(RegisterRequest request) {

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        user = userRepository.save(user);

        Role defaultRole = roleRepository.findByName(request.getRole()) //ROLE_USER
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        UserHasRole userHasRole = UserHasRole.builder()
                .user(user)
                .role(defaultRole)
                .build();

        userHasRoleRepository.save(userHasRole);
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

    @Override
    public UserDTO convertToDto(User user) {
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

}
