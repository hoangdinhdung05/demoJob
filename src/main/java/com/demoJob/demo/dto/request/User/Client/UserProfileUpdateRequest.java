package com.demoJob.demo.dto.request.User.Client;

import com.demoJob.demo.util.enums.Gender;
import com.demoJob.demo.util.validator.PhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Schema(description = "Yêu cầu cập nhật hồ sơ người dùng")
public class UserProfileUpdateRequest {

    @Schema(description = "URL ảnh đại diện của người dùng", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "Địa chỉ của người dùng", example = "123 Đường ABC, Quận 1, TP.HCM")
    private String address;

    @Schema(description = "Số điện thoại của người dùng", example = "0123456789")
    @PhoneNumber
    private String phone;

    @Schema(description = "Ngày sinh của người dùng", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "Giới tính của người dùng", example = "MALE")
    private Gender gender;

    @Schema(description = "URL trang web cá nhân của người dùng", example = "https://example.com")
    private String website;
}
