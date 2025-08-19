package com.demoJob.demo.service.CompanyService;

import com.demoJob.demo.dto.request.Company.CompanyRequest;
import com.demoJob.demo.dto.response.Company.CompanyResponse;

public interface UserCompanyService {

    /**
     * HR(User) tạo ra company
     *
     * @param request thông tin company
     * @return trả về thông tin sau khi đã tạo
     */
    CompanyResponse createCompany(CompanyRequest request);

}
