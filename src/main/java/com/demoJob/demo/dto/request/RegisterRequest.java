package com.demoJob.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Yêu cầu đăng ký người dùng")
public class RegisterRequest {

    @Schema(description = "First name", example = "Hoang")
    @NotBlank(message = "Họ không được để trống")
    private String firstName;

    @Schema(description = "Last name", example = "Dung")
    @NotBlank(message = "Tên không được để trống")
    private String lastName;

    @Schema(description = "Tên đăng nhập duy nhất", example = "john_doe")
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 4, max = 20, message = "Tên đăng nhập phải từ 4 đến 20 ký tự")
    private String username;

    @Schema(description = "Email của user", example = "exmple@gmail.com")
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Schema(description = "Mật khẩu của user", example = "P@ssw0rd!")
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 30, message = "Mật khẩu phải từ 8 đến 30 ký tự")
    private String password;
}
