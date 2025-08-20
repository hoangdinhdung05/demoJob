package com.demoJob.demo.dto.request.Company;

import com.demoJob.demo.util.enums.CompanyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateCompanyStatusRequest {

    @NotNull(message = "Status not null")
    private CompanyStatus status;

    private String reason;

}
