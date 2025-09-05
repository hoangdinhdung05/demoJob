package com.demoJob.demo.service.CompanyService;

import com.demoJob.demo.dto.request.Company.CompanyRequest;
import com.demoJob.demo.dto.request.Company.CompanyUpdateRequest;
import com.demoJob.demo.dto.response.Company.CompanyDetailResponse;
import com.demoJob.demo.dto.response.Company.CompanyResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.util.enums.CompanyStatus;

public interface CompanyService {

    /**
     * HR(User) tạo ra company
     *
     * @param request thông tin company
     * @return trả về thông tin sau khi đã tạo
     */
    CompanyResponse createCompany(CompanyRequest request);

    /**
     * Dùng cho user có quyền update thông tin company
     *
     * @param request thông tin cần update
     * @return trả về thông tin sau update
     */
    CompanyDetailResponse updateCompany(CompanyUpdateRequest request);


    /**
     * Hiển thị thông tin cơ bản trên bảng tin cho ứng viên xem
     *
     * @param companyId id company hiển thị
     * @return trả về thông tin cơ bản
     */
    CompanyResponse getCompanyById(Long companyId);

    /**
     * Xem thông tin chi tiết của company đó
     *
     * @param companyId id company hiển thị
     * @return trả về thông tin chi tiết
     */
    CompanyDetailResponse getDetailsCompany(Long companyId);

    /**
     * Ứng viên xem danh sách companies
     *
     * @param page Trang
     * @param size kích thước
     * @return trả về thông tin có phân trang
     */
    PageResponse<?> getAllCompanies(int page, int size);

    /**
     * Dùng cho việc update status company
     * @param companyId id company cần update
     * @param newStatus status cần đổi
     * @param reason lí do từ chối
     */
    void updateCompanyStatus(Long companyId, CompanyStatus newStatus, String reason);

    /**
     * Admin xóa company
     *
     * @param companyId id company
     */
    void deleteCompany(Long companyId);
}
