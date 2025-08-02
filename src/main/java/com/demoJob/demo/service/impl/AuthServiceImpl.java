package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.UserDTO;
import com.demoJob.demo.dto.request.Admin.ResetPasswordRequest;
import com.demoJob.demo.dto.request.LoginRequest;
import com.demoJob.demo.dto.request.Admin.RefreshTokenRequest;
import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.response.AuthResponse;
import com.demoJob.demo.dto.response.TokenRefreshResponse;
import com.demoJob.demo.dto.response.VerifyOtpRequest;
import com.demoJob.demo.entity.RefreshToken;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.exception.InvalidOtpException;
import com.demoJob.demo.exception.InvalidTokenException;
import com.demoJob.demo.exception.TokenBlacklistedException;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.JwtTokenProvider;
import com.demoJob.demo.service.*;
import com.demoJob.demo.util.OtpType;
import com.demoJob.demo.util.TokenBlacklistReason;
import jakarta.servlet.http.HttpServletRequest;
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

import static com.demoJob.demo.mapper.AuthMapper.toResponse;

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

    // Đăng nhập bằng username & password, sinh access + refresh token
    @Override
    public AuthResponse authenticateUser(LoginRequest request) {
        User user = authenticateAndGetUser(request.getUsername(), request.getPassword());
        checkEmailVerifier(user);
        return generateAuthResponse(user);
    }

    // Đăng ký user và gửi OTP xác minh email
    @Override
    public UserDTO register(RegisterRequest request) {
        UserDTO createUser = userService.createUser(request);
        User user = getUserByEmail(createUser.getEmail());

        otpService.sendOtp(user, SendOtpRequest.builder()
                .email(user.getEmail())
                .type(OtpType.VERIFY_EMAIL)
                .build());

        return createUser;
    }

    // Refresh access token từ refresh token
    @Override
    public TokenRefreshResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String reqToken = refreshTokenRequest.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(reqToken)
                .orElseThrow(() -> new TokenBlacklistedException("Invalid refresh token"));

        if (!refreshTokenService.isValid(refreshToken)) {
            throw new RuntimeException("Refresh token expired or revoked");
        }

        if (blacklistService.isBlacklisted(reqToken)) {
            throw new TokenBlacklistedException("Refresh token is blacklisted");
        }

        User user = refreshToken.getUser();
        String accessToken = jwtTokenProvider.generateAccessToken(user);

        return TokenRefreshResponse.builder()
                .accessToken(accessToken)
                .refreshToken(reqToken)
                .build();
    }

    // Logout: revoke refresh token và blacklist access token
    @Override
    public String logout(HttpServletRequest request) {
        String accessToken = extractToken(request);
        String username = jwtTokenProvider.getUsernameFromAccessToken(accessToken);
        if (username == null) throw new TokenBlacklistedException("Invalid access token");

        User user = getUserByUsername(username);
        refreshTokenService.revokeTokenByUser(user);

        Instant expiry = jwtTokenProvider.getAccessTokenExpiry(accessToken)
                .atZone(ZoneId.systemDefault()).toInstant();
        blacklistService.blacklistToken(accessToken, expiry, TokenBlacklistReason.LOGOUT);

        return "Logout successful";
    }

    // Xác minh email sau khi đăng ký
    @Override
    public String verifyEmail(String email, String code) {
        validateOtp(email, code, OtpType.VERIFY_EMAIL);
        return "Đã verify email thành công, vui lòng đăng nhập để tiếp tục";
    }

    // Gửi lại mã xác minh email
    @Override
    public void resendVerificationOtp(String email) {
        resendOtp(email, OtpType.VERIFY_EMAIL, true);
        log.info("Resent verification OTP to email: {}", email);
    }

    // Gửi lại mã OTP reset password
    @Override
    public void resendResetPasswordOtp(String email) {
        resendOtp(email, OtpType.RESET_PASSWORD, false);
        log.info("Resent reset password OTP to email: {}", email);
    }

    // Xác minh mã OTP quên mật khẩu
    @Override
    public void verifyForgotPasswordOtp(String email, String code) {
        validateOtp(email, code, OtpType.RESET_PASSWORD);
    }

    // Reset mật khẩu mới sau khi xác minh OTP
    @Override
    public String resetPassword(ResetPasswordRequest request) {
        validateOtp(request.getEmail(), request.getOtp(), OtpType.RESET_PASSWORD);

        User user = getUserByEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Mật khẩu đã được cập nhật thành công";
    }

    //=====//=====//=====//=====//

    private User authenticateAndGetUser(String username, String password) {
        log.info("Authenticate and get user with username={}", username);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return getUserByUsername(username);
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());
        refreshTokenService.createRefreshToken(user, refreshToken, jwtTokenProvider.getRefreshTokenExpiryDate());
        return toResponse(user, accessToken, refreshToken);
    }

    private void resendOtp(String email, OtpType type, boolean skipVerifiedCheck) {
        User user = getUserByEmail(email);

        if (!skipVerifiedCheck && type != OtpType.RESET_PASSWORD && user.getEmailVerified()) {
            throw new InvalidDataException("Email đã được xác minh");
        }

        otpService.sendOtp(user, SendOtpRequest.builder()
                .email(user.getEmail())
                .type(type)
                .build());
    }

    private void validateOtp(String email, String code, OtpType type) {
        boolean isValid = otpService.verifyOtp(VerifyOtpRequest.builder()
                .email(email)
                .code(code)
                .type(type)
                .build());

        if (!isValid) {
            throw new InvalidOtpException("OTP không hợp lệ hoặc đã hết hạn");
        }
    }

    private void checkEmailVerifier(User user) {
        if (!user.getEmailVerified()) {
            throw new InvalidDataException("Vui lòng xác minh email trước khi đăng nhập.");
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidDataException("Email không tồn tại"));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private User getVerifiedUserByEmail(String email) {
        User user = getUserByEmail(email);
        checkEmailVerifier(user);
        return user;
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new InvalidTokenException("Token not provided");
        }
        return header.substring(7);
    }
}
