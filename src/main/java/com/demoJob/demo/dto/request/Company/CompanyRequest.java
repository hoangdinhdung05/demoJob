package com.demoJob.demo.dto.request.Company;

import com.demoJob.demo.util.enums.CompanyStatus;
import com.demoJob.demo.util.validator.PhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(name = "CompanyRequest", description = "Request DTO để tạo mới công ty")
public class CompanyRequest {

    @Schema(description = "Tên công ty", example = "ABC Company")
    @NotBlank(message = "Name company not null")
    private String name;

    @Schema(description = "Logo công ty (URL)", example = "https://example.com/logo.png")
    private String logo;

    @Schema(description = "Email công ty", example = "example@gmail.com")
    @NotBlank(message = "Company email not null")
    private String email;

    @Schema(description = "Số điện thoại công ty", example = "+84 912345678")
    @PhoneNumber
    private String phone;

    @Schema(description = "URL website công ty", example = "https://example.com")
    private String website;

    @Schema(description = "Trạng thái công ty", example = "ACTIVE")
    private CompanyStatus status;
}
