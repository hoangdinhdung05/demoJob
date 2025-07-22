package base_webSocket_demo.service;

import base_webSocket_demo.dto.request.Admin.Company.CompanyRequest;
import base_webSocket_demo.dto.response.Admin.Company.CompanyResponse;
import base_webSocket_demo.dto.response.system.PageResponse;
import base_webSocket_demo.entity.Company;
import base_webSocket_demo.util.CompanyStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CompanyService {

    CompanyResponse createCompany(CompanyRequest request);

    CompanyResponse updateCompany(long companyId, CompanyRequest request);

    CompanyResponse changeCompanyStatus(long companyId, CompanyStatus status);

    void deleteCompany(long companyId);

    CompanyResponse getCompanyByCompanyId(long companyId);

    List<CompanyResponse> getAllCompanys(Specification<Company> specification);

    PageResponse<?> getPageCompany(int page, int size);

    PageResponse<?> searchCompanies(String keyword, int page, int size);
}
