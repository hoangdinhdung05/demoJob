package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.request.*;
import com.demoJob.demo.dto.request.Admin.ResetPasswordRequest;
import com.demoJob.demo.dto.request.User.Client.ChangePasswordRequest;
import com.demoJob.demo.dto.response.AuthResponse;
import com.demoJob.demo.dto.response.TokenRefreshResponse;
import com.demoJob.demo.entity.Token;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.*;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.JwtTokenProvider;
import com.demoJob.demo.service.*;
import com.demoJob.demo.service.UserService.UserClientService;
import com.demoJob.demo.util.enums.OtpType;
import com.demoJob.demo.util.enums.UserStatus;
import io.micrometer.common.util.StringUtils;
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
import java.util.Objects;
import static com.demoJob.demo.mapper.AuthMapper.toResponse;
import static com.demoJob.demo.util.containts.AuthMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final BlacklistService blacklistService;
    private final AuthenticationManager authenticationManager;
    private final UserClientService userService;
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
        checkUserStatus(user);
        return generateAuthResponse(user);
    }

    /**
     * Đăng ký người dùng mới
     * Tạo tài khoản và gửi OTP xác minh email
     * @param request đối tượng chứa thông tin đăng ký
     */
    @Override
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã được sử dụng");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Tên đăng nhập đã được sử dụng");
        }

        //Tạo người dùng mới
        userService.createUser(request);

        otpService.sendOtp(SendOtpRequest.builder()
                .email(request.getEmail())
                .build(), OtpType.VERIFY_EMAIL);
    }

    /**
     * Làm mới access token bằng refresh token
     * @param request refresh token
     * @return TokenRefreshResponse chứa access token và refresh token mới
     */
    @Override
    public TokenRefreshResponse refreshToken(RefreshTokenRequest request) {
        final String refreshToken = request.getRefreshToken();

        isValidRefreshToken(refreshToken);

        final String username = jwtTokenProvider.getUsernameFromRefreshToken(refreshToken);
        var user = getUserByUsername(username);

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        buildToken(username, accessToken, refreshToken);

        return TokenRefreshResponse.builder()
                .accessToken(accessToken)
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

        tokenService.delete(username);
        return LOGOUT_SUCCESS;
    }

    /**
     * Xác minh email người dùng
     * @param request đối tượng chứa thông tin xác minh email
     * @return thông báo xác minh email thành công
     */
    @Override
    public String active(VerifyOtpRequest request) {
        otpService.verifyEmail(request);
        return ACTIVE_SUCCESS;
    }

    /**
     * Gửi OTP đến email của người dùng để đặt lại mật khẩu
     * Kiểm tra xem email có tồn tại trong hệ thống hay không
     * Nếu tồn tại, gửi OTP và trả về thông báo thành công
     * @param request đối tượng chứa thông tin gửi OTP
     * @return thông báo gửi OTP thành công
     */
    @Override
    public String forgotPassword(SendOtpRequest request) {
        otpService.sendOtp(request, OtpType.RESET_PASSWORD);
        return FORGOT_PASSWORD_SUCCESS;
    }

    /**
     * User thay đổi mật khẩu của chính mình.
     * @param request thông tin thay đổi mật khẩu
     * @return thông báo thay đổi mật khẩu thành công
     */
    @Override
    public String changeMyPassword(ChangePasswordRequest request) {
        log.info("AuthService - Forwarding change password request");
        userService.changeMyPassword(request);
        return CHANGE_PASSWORD_SUCCESS;
    }

    /**
     * Xác minh OTP được gửi đến email người dùng
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
     * @return thông báo đặt lại mật khẩu thành công
     */
    @Override
    public String resetPassword(ResetPasswordRequest request) {

        //Validate password reset request
        if (!Objects.equals(request.getConfirmPassword(), request.getNewPassword())) {
            throw new InvalidDataException("Mật khẩu xác nhận không khớp");
        }

        //check verifyKey
        User user = otpService.confirmVerifyKey(request.getVerifyKey());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return RESET_PASSWORD_SUCCESS;
    }


    //====================== PRIVATE METHODS ====================//

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
        buildToken(user.getUsername(), accessToken, refreshToken);
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

    private void checkUserStatus(User user) {
        if (user.getStatus() == UserStatus.DELETE) {
            throw new InvalidDataException("Tài khoản không tồn tại hoặc đã bị xóa.");
        }
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

    /**
     * Lưu thông tin token xuống db
     */
    private void buildToken(String username, String accessToken, String refreshToken) {
        tokenService.save(Token.builder()
                .username(username)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
    }

    /**
     * Kiểm tra tính hợp lệ của refresh token
     * @param refreshToken refresh token cần kiểm tra
     * @throws InvalidDataException nếu token trống
     * @throws InvalidTokenException nếu token không hợp lệ hoặc hết hạn
     * @throws TokenBlacklistedException nếu token bị liệt vào danh sách đen
     */
    private void isValidRefreshToken(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        if (blacklistService.isBlacklisted(refreshToken)) {
            throw new TokenBlacklistedException("Refresh token is blacklisted");
        }
    }
}
