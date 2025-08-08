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
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.exception.InvalidOtpException;
import com.demoJob.demo.exception.TokenBlacklistedException;
import com.demoJob.demo.mapper.AuthMapper;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.JwtTokenProvider;
import com.demoJob.demo.service.*;
import com.demoJob.demo.util.OtpType;
import com.demoJob.demo.util.TokenBlacklistReason;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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

    @Override
    public AuthResponse authenticateUser(LoginRequest request) {

        User user = authenticateAndGetUser(request.getUsername(), request.getPassword());
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        return toResponse(user, accessToken, refreshToken);
    }

    @Override
    public UserDTO register(RegisterRequest request) {
        return userService.createUser(request);

    }

    @Override
    public TokenRefreshResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String reqToken = refreshTokenRequest.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(reqToken)
                .orElseThrow(() -> new TokenBlacklistedException("Invalid refresh token"));

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
        if (username == null) throw new TokenBlacklistedException("Invalid access token");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        refreshTokenService.revokeTokenByUser(user);
        Instant expiry = jwtTokenProvider.getAccessTokenExpiry(accessToken)
                .atZone(ZoneId.systemDefault()).toInstant();
        blacklistService.blacklistToken(accessToken, expiry, TokenBlacklistReason.LOGOUT);

        return "Logout successful";
    }

    //
    @Override
    public void loginWithOtp(LoginEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidDataException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Inc");
        }

        otpService.sendOtp(SendOtpRequest.builder()
                .email(user.getEmail())
                .type(OtpType.LOGIN)
                .build());
    }

    //Active luôn email
    @Override
    public AuthResponse verifyOtpAndLogin(String email, String code) {
        boolean isValid = otpService.verifyOtp(VerifyOtpRequest.builder()
                .email(email)
                .code(code)
                .type(OtpType.LOGIN)
                .build());

        if (!isValid) {
            throw new InvalidOtpException("OTP không hợp lệ hoặc đã hết hạn");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());
        return toResponse(user, accessToken, refreshToken);
    }

    private User authenticateAndGetUser(String username, String password) {

        log.info("Authenticate and get user with username={}", username);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );
        //Sau khi check xong ok hett dung SecurityContext de luu lai thong tin nhu username, password, tokn
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //tra ve thong tin user
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
