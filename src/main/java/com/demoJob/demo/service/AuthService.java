package com.demoJob.demo.service;

import com.demoJob.demo.dto.UserDTO;
import com.demoJob.demo.dto.request.Admin.ResetPasswordRequest;
import com.demoJob.demo.dto.request.LoginRequest;
import com.demoJob.demo.dto.request.Admin.RefreshTokenRequest;
import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.request.VerifyOtpRequest;
import com.demoJob.demo.dto.response.AuthResponse;
import com.demoJob.demo.dto.response.TokenRefreshResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    /**
     * Xác thực người dùng với thông tin đăng nhập
     * @param request chứa thông tin đăng nhập (username, password)
     * @return trả về thông tin xác thực người dùng bao gồm access token, refresh token và thông tin người dùng
     */
    AuthResponse authenticateUser(LoginRequest request);

    /**
     * Đăng ký người dùng mới
     * @param request chứa thông tin đăng ký (họ, tên, tên đăng nhập, email, mật khẩu)
     * @return trả về thông tin người dùng đã đăng ký
     */
    UserDTO register(RegisterRequest request);


    /**
     * Xác minh verifyKey để lấy thông tin người dùng
     * @param verifyKey mã xác minh được gửi qua OTP
     * @return UserDTO nếu xác minh thành công
     */
    String confirmVerifyEmail(String verifyKey);

    /**
     * Đặt lại mật khẩu cho người dùng
     * @param request chứa thông tin đặt lại mật khẩu (verifyKey, mật khẩu mới)
     * @return thông báo thành công
     */
    String resetPassword(ResetPasswordRequest request);

    /**
     * Làm mới token truy cập bằng refresh token
     * @param refreshTokenRequest chứa thông tin refresh token
     * @return TokenRefreshResponse chứa access token mới
     */
    TokenRefreshResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    /**
     * Đăng xuất người dùng
     * @param request chứa thông tin yêu cầu đăng xuất
     * @return thông báo đăng xuất thành công
     */
    String logout(HttpServletRequest request);

}
