package com.demoJob.demo.dto.request.Company;

import com.demoJob.demo.util.enums.CompanyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(name = "UpdateCompanyStatusRequest", description = "Request DTO để cập nhật trạng thái công ty")
public class UpdateCompanyStatusRequest {

    @Schema(description = "Trạng thái công ty", example = "ACTIVE")
    @NotNull(message = "Status not null")
    private CompanyStatus status;

    @Schema(description = "Lý do thay đổi trạng thái", example = "Công ty hoạt động tốt")
    private String reason;
}
