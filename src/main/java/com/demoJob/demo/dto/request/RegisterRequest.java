package com.demoJob.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {

    @Size(min = 2, max = 50, message = "Tên phải từ 2 đến 50 ký tự")
    private String firstName;

    @Size(min = 2, max = 50, message = "Họ phải từ 2 đến 50 ký tự")
    private String lastName;

    @Size(min = 6, max = 20, message = "Tên người dùng phải từ 6 đến 20 ký tự")
    private String username;

    @Email(message = "Email không hợp lệ")
    private String email;

    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;
}