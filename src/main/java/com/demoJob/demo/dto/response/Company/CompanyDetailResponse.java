package com.demoJob.demo.dto.response.Company;

import com.demoJob.demo.util.enums.CompanyStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyDetailResponse {

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
