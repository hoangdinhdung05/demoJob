package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.Admin.Company.CompanyRequest;
import com.demoJob.demo.dto.response.Admin.Company.CompanyResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.util.CompanyStatus;

import java.util.List;

public interface CompanyService {

    CompanyResponse createCompany(CompanyRequest request);

    CompanyResponse updateCompany(long companyId, CompanyRequest request);

    CompanyResponse changeCompanyStatus(long companyId, CompanyStatus status);

    void deleteCompany(long companyId);

    CompanyResponse getCompanyByCompanyId(long companyId);

    List<CompanyResponse> getAllCompanys();

    PageResponse<?> getPageCompany(int page, int size);

    PageResponse<?> searchCompanies(String keyword, int page, int size);
}
