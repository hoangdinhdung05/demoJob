package com.demoJob.demo.service.CompanyService;

import com.demoJob.demo.util.enums.CompanyStatus;

public interface AdminCompanyService {

    /**
     * Dùng cho việc update status company
     * @param companyId id company cần update
     * @param newStatus status cần đổi
     * @param reason lí do từ chối
     */
    void updateCompanyStatus(Long companyId, CompanyStatus newStatus, String reason);

}
