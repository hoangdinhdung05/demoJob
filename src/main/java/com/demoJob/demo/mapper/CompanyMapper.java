package com.demoJob.demo.mapper;

import com.demoJob.demo.dto.request.Company.CompanyUpdateRequest;
import com.demoJob.demo.dto.response.Admin.Job.CompanyJobResponse;
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

    public static CompanyJobResponse toResponseJob(Company company) {
        return CompanyJobResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .build();
    }

    public static void updateCompanyRequest(CompanyUpdateRequest request, Company company, boolean isAdmin) {
        if (request.getName() != null) {
            company.setName(request.getName());
        }
        if (request.getEmail() != null) {
            company.setEmail(request.getEmail());
        }
        if (request.getLogo() != null) {
            company.setLogo(request.getLogo());
        }
        if (request.getPhone() != null) {
            company.setPhone(request.getPhone());
        }
        if (request.getWebsite() != null) {
            company.setWebsite(request.getWebsite());
        }

        if (isAdmin && request.getStatus() != null) {
            company.setStatus(request.getStatus());
        }

        CompanyProfile profile = company.getProfile();
        if (request.getDescription() != null) profile.setDescription(request.getDescription());
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getCountry() != null) profile.setCountry(request.getCountry());
        if (request.getIndustry() != null) profile.setIndustry(request.getIndustry());
        if (request.getTaxCode() != null) profile.setTaxCode(request.getTaxCode());
        if (request.getCompanySize() != null) profile.setCompanySize(request.getCompanySize());
        if (request.getWorkingTime() != null) profile.setWorkingTime(request.getWorkingTime());
        if (request.getMapLocation() != null) profile.setMapLocation(request.getMapLocation());
    }
}
