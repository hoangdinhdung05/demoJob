package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.UserDTO;
import com.demoJob.demo.dto.request.LoginEmailRequest;
import com.demoJob.demo.dto.request.LoginRequest;
import com.demoJob.demo.dto.request.Admin.RefreshTokenRequest;
import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.response.AuthResponse;
import com.demoJob.demo.dto.response.TokenRefreshResponse;
import com.demoJob.demo.dto.response.VerifyOtpRequest;
import com.demoJob.demo.entity.RefreshToken;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.JwtTokenProvider;
import com.demoJob.demo.service.*;
import com.demoJob.demo.util.OtpType;
import com.demoJob.demo.util.TokenBlacklistReason;
import com.demoJob.demo.util.TokenType;
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
        log.info("Authenticating user: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return generateAuthResponse(user);
    }

    @Override
    public UserDTO register(RegisterRequest request) {
        return userService.createUser(request);
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

        refreshTokenService.revokeTokenByUser(user);
        Instant expiry = jwtTokenProvider.getAccessTokenExpiry(accessToken)
                .atZone(ZoneId.systemDefault()).toInstant();
        blacklistService.blacklistToken(accessToken, expiry, TokenBlacklistReason.LOGOUT);

        return "Logout successful";
    }

    @Override
    public void loginWithOtp(LoginEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

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

        return generateAuthResponse(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
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
                        .map(role -> role.getRole().getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}
