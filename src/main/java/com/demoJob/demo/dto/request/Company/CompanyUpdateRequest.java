package com.demoJob.demo.dto.request.Company;

import com.demoJob.demo.util.enums.CompanyStatus;
import lombok.Getter;

@Getter
public class CompanyUpdateRequest {

    private Long id;

    private String name;

    private String logo;

    private String email;

    private String phone;

    private String website;

    private CompanyStatus status;

    private String description;

    private String address;

    private String city;

    private String country;

    private String industry;

    private String taxCode;

    private String companySize;

    private String workingTime;

    private String mapLocation;
}
