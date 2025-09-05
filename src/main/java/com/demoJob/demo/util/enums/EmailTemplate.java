package com.demoJob.demo.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTemplate {
    COMPANY_REGISTRATION_ADMIN("company-registration-admin"),
    COMPANY_APPROVED_OWNER("company-approved-owner"),
    COMPANY_REJECTED_OWNER("company-rejected-owner"),
    COMPANY_BACK_TO_PENDING("company-back-to-pending");

    private final String fileName;
}
