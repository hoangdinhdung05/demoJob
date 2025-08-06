package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.UserDTO;
import com.demoJob.demo.dto.request.Admin.ResetPasswordRequest;
import com.demoJob.demo.dto.request.LoginRequest;
import com.demoJob.demo.dto.request.Admin.RefreshTokenRequest;
import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.response.AuthResponse;
import com.demoJob.demo.dto.response.TokenRefreshResponse;
import com.demoJob.demo.dto.request.VerifyOtpRequest;
import com.demoJob.demo.entity.RefreshToken;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.*;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.JwtTokenProvider;
import com.demoJob.demo.service.*;
import com.demoJob.demo.util.OtpType;
import com.demoJob.demo.util.TokenBlacklistReason;
import com.demoJob.demo.util.UserStatus;
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
import java.util.Objects;

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


    /**
     * Xác thực người dùng và trả về thông tin đăng nhập
     * @param request đối tượng chứa thông tin đăng nhập
     * @return AuthResponse chứa access token, refresh token và thông tin người dùng
     */
    @Override
    public AuthResponse authenticateUser(LoginRequest request) {
        User user = authenticateAndGetUser(request.getUsername(), request.getPassword());
        checkEmailVerifier(user);
        return generateAuthResponse(user);
    }

    /**
     * Đăng ký người dùng mới
     * Tạo tài khoản và gửi OTP xác minh email
     * @param request đối tượng chứa thông tin đăng ký
     * @return UserDTO chứa thông tin người dùng đã đăng ký
     */
    @Override
    public UserDTO register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã được sử dụng");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Tên đăng nhập đã được sử dụng");
        }

        UserDTO createUser = userService.createUser(request);
        User user = getUserByEmail(createUser.getEmail());

        otpService.sendOtp(user, SendOtpRequest.builder()
                .email(user.getEmail())
                .type(OtpType.VERIFY_EMAIL)
                .build());

        return createUser;
    }

    /**
     * Làm mới access token bằng refresh token
     * @param refreshTokenRequest đối tượng chứa refresh token
     * @return TokenRefreshResponse chứa access token và refresh token mới
     */
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

    /**
     * Đăng xuất người dùng
     * Đăng xuất bằng cách thu hồi refresh token và blacklist access token
     * @param request đối tượng chứa thông tin yêu cầu HTTP
     * @return Thông báo đăng xuất thành công
     */
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

    /**
     * Xác minh email người dùng
     * @param request đối tượng chứa thông tin xác minh email
     */
    //Xác minh email luôn bằng OTP mà không cần thông qua key
    @Override
    public void active(VerifyOtpRequest request) {
        otpService.verifyEmail(request);
    }

    /**
     * Gửi OTP đến email của người dùng để đặt lại mật khẩu
     * Kiểm tra xem email có tồn tại trong hệ thống hay không
     * Nếu tồn tại, gửi OTP và trả về thông báo thành công
     * @param request đối tượng chứa thông tin gửi OTP
     */
    @Override
    public void forgotPassword(SendOtpRequest request) {
        User user = getUserByEmail(request.getEmail().trim().toLowerCase());
        otpService.sendOtp(user, request);
    }

    /**
     * Xác minh OTP được gửi đến email người dùng
     *
     * @param request chứa thông tin xác minh OTP (email, loại OTP, mã OTP)
     * @return verifyKey nếu xác minh thành công
     */
    @Override
    public String verifyResetPassword(VerifyOtpRequest request) {
        return otpService.verifyOtp(request);
    }

    /**
     * Đặt lại mật khẩu cho người dùng
     * Xác minh verifyKey và cập nhật mật khẩu mới
     * @param request đối tượng chứa thông tin đặt lại mật khẩu
     * @return Thông báo đặt lại mật khẩu thành công
     */
    @Override
    public String resetPassword(ResetPasswordRequest request) {
        //check verifyKey
        User user = otpService.confirmVerifyKey(request.getVerifyKey(), OtpType.RESET_PASSWORD);

        //Validate password reset request
        if (!Objects.equals(request.getConfirmPassword(), request.getNewPassword())) {
            throw new InvalidDataException("Mật khẩu xác nhận không khớp");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Mật khẩu đã được đặt lại thành công.";
    }


    //=====//=====//=====//=====//

    /**
     * Xác thực người dùng bằng tên đăng nhập và mật khẩu
     * @param username tên đăng nhập của người dùng
     * @param password mật khẩu của người dùng
     * @return User đối tượng người dùng đã xác thực
     */
    private User authenticateAndGetUser(String username, String password) {
        log.info("Authenticate and get user with username={}", username);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return getUserByUsername(username);
    }

    /**
     * Tạo AuthResponse chứa access token và refresh token
     * và lưu refresh token vào cơ sở dữ liệu
     * @param user đối tượng người dùng đã xác thực
     * @return AuthResponse chứa thông tin đăng nhập
     */
    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());
        refreshTokenService.createRefreshToken(user, refreshToken, jwtTokenProvider.getRefreshTokenExpiryDate());
        return toResponse(accessToken, refreshToken);
    }

    /**
     * Kiểm tra xem người dùng đã xác minh email hay chưa
     * Nếu chưa xác minh, sẽ ném ra InvalidDataException
     * @param user đối tượng người dùng cần kiểm tra
     */
    private void checkEmailVerifier(User user) {
        if (!user.getEmailVerified()) {
            throw new InvalidDataException("Vui lòng xác minh email trước khi đăng nhập.");
        }
    }

    /**
     * Lấy thông tin người dùng từ email
     * Nếu email không tồn tại, sẽ ném ra InvalidDataException
     * @param email địa chỉ email của người dùng
     * @return User đối tượng người dùng tương ứng với email
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidDataException("Email không tồn tại"));
    }

    /**
     * Lấy thông tin người dùng từ tên đăng nhập
     * Nếu tên đăng nhập không tồn tại, sẽ ném ra UsernameNotFoundException
     * @param username tên đăng nhập của người dùng
     * @return User đối tượng người dùng tương ứng với tên đăng nhập
     */
    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Trích xuất access token từ header Authorization
     * Nếu không có token hoặc định dạng không hợp lệ, sẽ ném ra InvalidTokenException
     * @param request đối tượng HttpServletRequest chứa header Authorization
     * @return access token nếu hợp lệ
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new InvalidTokenException("Token not provided");
        }
        return header.substring(7);
    }
}
