package com.demoJob.demo.mapper;

import com.demoJob.demo.dto.response.Company.CompanyDetailResponse;
import com.demoJob.demo.dto.response.Company.CompanyResponse;
import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.CompanyProfile;

public class CompanyMapper {

    public static CompanyResponse toResponse(Company company) {
        if (company == null) return null;

        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .logo(company.getLogo())
                .email(company.getEmail())
                .phone(company.getPhone())
                .website(company.getWebsite())
                .status(company.getStatus())
                .build();
    }

    public static CompanyDetailResponse toDetailsResponse(Company company) {
        CompanyProfile profile = company.getProfile();

        return CompanyDetailResponse.builder()
                .name(company.getName())
                .logo(company.getLogo())
                .email(company.getEmail())
                .phone(company.getPhone())
                .website(company.getWebsite())
                .status(company.getStatus())
                .description(profile != null ? profile.getDescription() : null)
                .address(profile != null ? profile.getAddress() : null)
                .companySize(profile != null ? profile.getCompanySize() : null)
                .mapLocation(profile != null ? profile.getMapLocation() : null)
                .taxCode(profile != null ? profile.getTaxCode() : null)
                .build();
    }
}
