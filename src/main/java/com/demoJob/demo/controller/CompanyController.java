package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Company.CompanyRequest;
import com.demoJob.demo.dto.request.Company.CompanyUpdateRequest;
import com.demoJob.demo.dto.request.Company.UpdateCompanyStatusRequest;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "COMPANY", description = "Quản lý công ty")
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Tạo company mới
     * Cho phép: USER (HR) và ADMIN
     */
    @Operation(summary = "Tạo company mới", description = "Tạo company mới gửi thông báo đến ADMIN.")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HR', 'ROLE_ADMIN')")
    public ResponseEntity<?> createCompany(@RequestBody @Valid CompanyRequest request) {
        log.info("API create company - Request: {}", request.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseData<>(HttpStatus.CREATED.value(),
                        "Company created successfully", companyService.createCompany(request)));
    }

    /**
     * Cập nhật thông tin company
     * Cho phép: Owner của company hoặc ADMIN
     */
    @Operation(summary = "Cập nhật company", description = "Cập nhật thông tin company, chỉ owner của company hoặc ADMIN mới có quyền thực hiện.")
    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HR', 'ROLE_ADMIN')")
    public ResponseEntity<?> updateCompany(@RequestBody @Valid CompanyUpdateRequest request) {
        log.info("API update company - ID: {}", request.getId());
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "Company updated successfully", companyService.updateCompany(request)));
    }

    /**
     * Lấy thông tin cơ bản company theo ID
     * Cho phép: Tất cả user (public)
     */
    @Operation(summary = "Lấy company theo ID", description = "Lấy thông tin cơ bản của company theo ID, cho phép tất cả user truy cập.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Long id) {
        log.info("API get company by ID: {}", id);
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "Get company successfully", companyService.getCompanyById(id)));
    }

    /**
     * Lấy thông tin chi tiết company
     * Cho phép: Tất cả user (public)
     */
    @Operation(summary = "Lấy chi tiết company", description = "Lấy thông tin chi tiết của company theo ID, cho phép tất cả user truy cập.")
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getCompanyDetails(@PathVariable Long id) {
        log.info("API get company details - ID: {}", id);
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "Get company details successfully", companyService.getDetailsCompany(id)));
    }

    /**
     * Lấy danh sách tất cả companies (có phân trang)
     * Cho phép: Tất cả user (public)
     */
    @Operation(summary = "Lấy tất cả companies", description = "Lấy danh sách tất cả companies với phân trang, cho phép tất cả user truy cập.")
    @GetMapping
    public ResponseEntity<?> getAllCompanies(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("API get all companies - Page: {}, Size: {}", page, size);
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "Get companies successfully", companyService.getAllCompanies(page, size)));
    }

    /**
     * Cập nhật status company (approve/reject)
     * Cho phép: ADMIN và MANAGER
     */
    @Operation(summary = "Cập nhật trạng thái company", description = "Cập nhật trạng thái của company (approve/reject), chỉ ADMIN và MANAGER mới có quyền thực hiện.")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<?> updateCompanyStatus(@PathVariable Long id,
                                                 @RequestBody @Valid UpdateCompanyStatusRequest request) {
        log.info("API update company status - ID: {}, Status: {}", id, request.getStatus());
        companyService.updateCompanyStatus(id, request.getStatus(), request.getReason());
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "Company status updated successfully"));
    }

    /**
     * Xóa company status=DELETE
     * Cho phép Admin và Manager
     */
    @Operation(summary = "Xóa company", description = "Xóa company (chuyển trạng thái sang DELETE), chỉ ADMIN và MANAGER mới có quyền thực hiện.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
        log.info("API delete company ID: {}", id);
        companyService.deleteCompany(id);
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(), "Company deleted successfully")
        );
    }
}