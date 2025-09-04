package com.demoJob.demo.dto.request.User.Admin;

import com.demoJob.demo.util.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Set;

@Data
@Schema(description = "Yêu cầu cập nhật thông tin người dùng bởi Admin")
public class UserAdminUpdateRequest {

    @Schema(description = "Họ của người dùng", example = "Nguyen")
    private String firstName;

    @Schema(description = "Tên của người dùng", example = "An")
    private String lastName;

    @Schema(description = "Email của người dùng", example = "example@gmail.com")
    private String email;

    @Schema(description = "Roles của người dùng", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private Set<String> roles;

    @Schema(description = "Xác minh email của người dùng", example = "true")
    private Boolean emailVerified;

    @Schema(description = "Trạng thái của người dùng", example = "ACTIVE")
    private UserStatus status;
}