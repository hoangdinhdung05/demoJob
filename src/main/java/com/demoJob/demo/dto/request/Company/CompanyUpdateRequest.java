package com.demoJob.demo.dto.request.Company;

import com.demoJob.demo.util.enums.CompanyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "CompanyUpdateRequest", description = "Request DTO for updating company information")
public class CompanyUpdateRequest {

    @Schema(description = "ID of the company", example = "1")
    private Long id;

    @Schema(description = "Name of the company", example = "OpenAI Vietnam")
    private String name;

    @Schema(description = "Logo URL of the company", example = "https://example.com/logo.png")
    private String logo;

    @Schema(description = "Email address of the company", example = "contact@openai.vn")
    private String email;

    @Schema(description = "Phone number of the company", example = "+84 912345678")
    private String phone;

    @Schema(description = "Website URL of the company", example = "https://openai.vn")
    private String website;

    @Schema(description = "Status of the company", example = "ACTIVE")
    private CompanyStatus status;

    @Schema(description = "Description about the company", example = "AI research and development company in Vietnam")
    private String description;

    @Schema(description = "Address of the company", example = "123 Nguyen Trai, District 1")
    private String address;

    @Schema(description = "City where the company is located", example = "Ho Chi Minh City")
    private String city;

    @Schema(description = "Country where the company is located", example = "Vietnam")
    private String country;

    @Schema(description = "Industry sector of the company", example = "Information Technology")
    private String industry;

    @Schema(description = "Company tax code", example = "0101234567")
    private String taxCode;

    @Schema(description = "Size of the company", example = "100-500 employees")
    private String companySize;

    @Schema(description = "Working time of the company", example = "Mon-Fri, 9:00 - 18:00")
    private String workingTime;

    @Schema(description = "Map location of the company", example = "10.762622, 106.660172")
    private String mapLocation;
}
