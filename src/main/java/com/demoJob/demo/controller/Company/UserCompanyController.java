package com.demoJob.demo.controller.Company;

import com.demoJob.demo.dto.request.Company.CompanyRequest;
import com.demoJob.demo.dto.response.Company.CompanyResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.CompanyService.UserCompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserCompanyController {

    private final UserCompanyService userCompanyService;

    @PostMapping
    public ResponseEntity<?> createCompany(@RequestBody @Valid CompanyRequest request) {
        log.info("[USER] Api create company");
        CompanyResponse response = userCompanyService.createCompany(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseData<>(HttpStatus.CREATED.value(), "Api create company successfully", response));
    }

}
