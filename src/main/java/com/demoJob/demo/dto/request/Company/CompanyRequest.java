package com.demoJob.demo.dto.request.Company;

import com.demoJob.demo.util.enums.CompanyStatus;
import com.demoJob.demo.util.validator.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CompanyRequest {

    @NotBlank(message = "Name company not null")
    private String name;

    private String logo;

    @NotBlank(message = "Company email not null")
    private String email;

    @PhoneNumber
    private String phone;

    private String website;

    private CompanyStatus status;
}
