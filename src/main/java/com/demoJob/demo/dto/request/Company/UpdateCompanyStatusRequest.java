package com.demoJob.demo.dto.request.Company;

import com.demoJob.demo.util.enums.CompanyStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateCompanyStatusRequest {

    @NotBlank(message = "Status not null")
    private CompanyStatus status;

    private String reason;

}
