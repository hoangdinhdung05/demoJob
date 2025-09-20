package com.demoJob.demo.dto.request;

import com.demoJob.demo.validator.EmailValidator.ValidEmail;
import com.demoJob.demo.validator.PasswordValidator.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Yêu cầu đăng ký người dùng")
public class RegisterRequest {

    @Schema(description = "First name", example = "Hoang")
    @NotBlank(message = "First name cannot null ")
    private String firstName;

    @Schema(description = "Last name", example = "Dung")
    @NotBlank(message = "Last name cannot null")
    private String lastName;

    @Schema(description = "Tên đăng nhập duy nhất", example = "john_doe")
    @NotBlank(message = "Username cannot null")
    @Size(min = 4, max = 20, message = "Tên đăng nhập phải từ 4 đến 20 ký tự")
    private String username;

    @Schema(description = "Email của user", example = "exmple@gmail.com")
    @NotBlank(message = "Email cannot null")
    @ValidEmail
    private String email;

    @Schema(description = "Mật khẩu của user", example = "P@ssw0rd!")
    @NotBlank(message = "Password cannot null")
    @Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters")
    @ValidPassword
    private String password;
}
