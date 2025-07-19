package base_webSocket_demo.service.impl;

import base_webSocket_demo.dto.UserDTO;
import base_webSocket_demo.dto.request.LoginRequest;
import base_webSocket_demo.dto.request.Admin.RefreshTokenRequest;
import base_webSocket_demo.dto.request.RegisterRequest;
import base_webSocket_demo.dto.response.AuthResponse;
import base_webSocket_demo.dto.response.TokenRefreshResponse;
import base_webSocket_demo.entity.User;
import base_webSocket_demo.security.JwtTokenProvider;
import base_webSocket_demo.service.AuthService;
import base_webSocket_demo.service.UserService;
import base_webSocket_demo.util.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Override
    public AuthResponse authenticateUser(LoginRequest request) {
        log.info("=====Authenticate User========");
        log.info("Loading user: {}", request.getUsername());

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String accessToken = jwtTokenProvider.generateAccessToken(authentication);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .tokenType(TokenType.ACCESS_TOKEN)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .roles(user.getUserHasRoles()
                            .stream()
                            .map(u -> u.getRole().getName())
                            .collect(Collectors.toSet()))
                    .build();

        } catch (Exception e) {
            log.error("Authenticate failed: {}", e.getMessage());
            throw new RuntimeException("Authentication failed");
        }
    }

    @Override
    public UserDTO register(RegisterRequest request) {

        UserDTO newUser = userService.createUser(request);

        return newUser;
    }

    @Override
    public TokenRefreshResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        return null;
    }

    @Override
    public String logout(String token) {
        return "";
    }

    private UserDTO convertUserDTO(User user) {
        return  UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
