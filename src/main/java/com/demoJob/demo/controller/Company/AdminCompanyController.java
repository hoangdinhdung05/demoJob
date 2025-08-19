package com.demoJob.demo.controller.Company;

import com.demoJob.demo.dto.request.Company.UpdateCompanyStatusRequest;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.CompanyService.AdminCompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/companies")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCompanyController {

    private final AdminCompanyService adminCompanyService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateCompanyStatus(
            @PathVariable Long id,
            @RequestBody UpdateCompanyStatusRequest request) {

        adminCompanyService.updateCompanyStatus(id, request.getStatus(), request.getReason());

        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "Company status updated successfully")
        );
    }


}
