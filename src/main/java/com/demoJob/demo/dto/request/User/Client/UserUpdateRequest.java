package com.demoJob.demo.dto.request.User.Client;

import com.demoJob.demo.util.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
@Schema(description = "Yêu cầu cập nhật thông tin người dùng")
public class UserUpdateRequest {

    @Schema(description = "First name", example = "Hoang")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Schema(description = "Last name", example = "Dung")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Schema(description = "Email of the user", example = "example@gmail.com")
    @Email(message = "Email should be valid")
    private String email;

    //Details
    @Schema(description = "Phone number", example = "0123456789")
    private String phone;

    @Schema(description = "Giới tính", example = "MALE")
    public Gender gender;

    @Schema(description = "Avatar URL", example = "http://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "Address", example = "123 Main St, City, Country")
    private String address;

    @Schema(description = "Birth date", example = "1990-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Schema(description = "Website URL", example = "http://example.com")
    private String website;

    //Company
    @Schema(description = "Company ID", example = "1")
    private Long companyId;
}
