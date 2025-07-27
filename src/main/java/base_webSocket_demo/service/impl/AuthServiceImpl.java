package base_webSocket_demo.service.impl;

import base_webSocket_demo.dto.UserDTO;
import base_webSocket_demo.dto.request.LoginEmailRequest;
import base_webSocket_demo.dto.request.LoginRequest;
import base_webSocket_demo.dto.request.Admin.RefreshTokenRequest;
import base_webSocket_demo.dto.request.RegisterRequest;
import base_webSocket_demo.dto.request.SendOtpRequest;
import base_webSocket_demo.dto.response.AuthResponse;
import base_webSocket_demo.dto.response.TokenRefreshResponse;
import base_webSocket_demo.dto.response.TokenResponse;
import base_webSocket_demo.dto.response.VerifyOtpRequest;
import base_webSocket_demo.entity.RefreshToken;
import base_webSocket_demo.entity.Role;
import base_webSocket_demo.entity.User;
import base_webSocket_demo.repository.UserRepository;
import base_webSocket_demo.security.JwtTokenProvider;
import base_webSocket_demo.service.*;
import base_webSocket_demo.util.OtpType;
import base_webSocket_demo.util.TokenBlacklistReason;
import base_webSocket_demo.util.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final BlacklistService blacklistService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

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

            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());
            refreshTokenService.createRefreshToken(user, refreshToken, jwtTokenProvider.getRefreshTokenExpiryDate());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType(TokenType.ACCESS_TOKEN)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .roles(user.getUserHasRoles()
                            .stream()
                            .map(u -> u.getRole().getName())
                            .collect(Collectors.toSet()))
                    .build();

        } catch (Exception e) {
            log.error("Authenticate failed", e);
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
        String reqToken = refreshTokenRequest.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(reqToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!refreshTokenService.isValid(refreshToken)) {
            throw new RuntimeException("Refresh token expired or revoked");
        }

        User user = refreshToken.getUser();
        String accessToken = jwtTokenProvider.generateAccessToken(user);

        return TokenRefreshResponse.builder()
                .accessToken(accessToken)
                .refreshToken(reqToken)
                .build();
    }

    @Override
    public String logout(String accessToken) {
        String username = jwtTokenProvider.getUsernameFromAccessToken(accessToken);
        if (username == null) throw new RuntimeException("Invalid access token");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenService.revokeTokenByUser(user); // ✅ revoke refresh token
        Instant expiry = jwtTokenProvider.getAccessTokenExpiry(accessToken).atZone(ZoneId.systemDefault()).toInstant();
        blacklistService.blacklistToken(accessToken, expiry, TokenBlacklistReason.LOGOUT); // ✅ blacklist access

        return "Logout successful";
    }

    @Override
    public void loginWithOtp(LoginEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        // Gửi OTP
        otpService.sendOtp(SendOtpRequest.builder()
                .email(user.getEmail())
                .type(OtpType.LOGIN)
                .build());
    }

    @Override
    public AuthResponse verifyOtpAndLogin(String email, String code) {
        boolean isValid = otpService.verifyOtp(VerifyOtpRequest.builder()
                .email(email)
                .code(code)
                .type(OtpType.LOGIN)
                .build());

        if (!isValid) {
            throw new RuntimeException("OTP không hợp lệ hoặc đã hết hạn");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        String token = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());
        return AuthResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType(TokenType.ACCESS_TOKEN)
                .userId(user.getId())
                .username(user.getUsername())
                .roles(user.getUserHasRoles()
                        .stream()
                        .map(u -> u.getRole().getName())
                        .collect(Collectors.toSet()))
                .build();
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

    //TODO: Thêm lý do FORCE_LOGOUT để admin block user từ backend
}
