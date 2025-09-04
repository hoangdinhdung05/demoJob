package com.demoJob.demo.dto.request.User.Admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
@Schema(description = "Yêu cầu tạo người dùng mới bởi quản trị viên")
public class AdminCreateUserRequest {

    @Schema(description = "Họ của người dùng", example = "Nguyễn")
    @NotBlank(message = "Họ không được để trống")
    private String firstName;

    @Schema(description = "Tên của người dùng", example = "Văn A")
    @NotBlank(message = "Tên không được để trống")
    private String lastName;

    @Schema(description = "Tên đăng nhập của người dùng", example = "nguyenvana")
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 4, max = 20, message = "Tên đăng nhập phải từ 4 đến 20 ký tự")
    private String username;

    @Schema(description = "Email của người dùng", example = "example@gmail.com")
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Schema(description = "Mật khẩu của người dùng", example = "P@ssw0rd")
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 30, message = "Mật khẩu phải từ 8 đến 30 ký tự")
    private String password;

    @Schema(description = "Vai trò của người dùng", example = "[\"USER\", \"ADMIN\"]")
    private Set<String> roles;
}
