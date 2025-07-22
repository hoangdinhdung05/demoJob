package base_webSocket_demo.controller;

import base_webSocket_demo.dto.request.Admin.Company.CompanyRequest;
import base_webSocket_demo.dto.response.Admin.Company.CompanyResponse;
import base_webSocket_demo.dto.response.system.PageResponse;
import base_webSocket_demo.dto.response.system.ResponseData;
import base_webSocket_demo.dto.response.system.ResponseError;
import base_webSocket_demo.entity.Company;
import base_webSocket_demo.service.CompanyService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/admin/create")
    public ResponseData<?> adminCreateCompany(@RequestBody @Valid CompanyRequest request) {
        log.info("Api admin create company");

        try {
            CompanyResponse response = companyService.createCompany(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Api admin create company successfully", response);
        } catch (Exception e) {
            log.error("Api admin create company error={}, cause={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Api admin create company fail");
        }
    }

    @GetMapping("/admin/{companyId}")
    public ResponseData<?> adminGetCompanyById(@PathVariable @Min(1) Long companyId) {
        log.info("Api admin get company by id={}", companyId);

        try {
            CompanyResponse response = companyService.getCompanyByCompanyId(companyId);
            return new ResponseData<>(HttpStatus.OK.value(), "Api admin get company by id successfully", response);
        } catch (Exception e) {
            log.error("Api admin get company by id error={}, cause={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Api admin get company by id fail");
        }
    }

    @GetMapping("/admin/getAllCompanyNoPage")
    public ResponseData<?> getAllCompany(@Filter Specification<Company> specification) {
        log.info("Api get no page company");

        try {

            List<CompanyResponse> response = companyService.getAllCompanys(specification);
            return new ResponseData<>(HttpStatus.OK.value(), "Api get all company no page successfully", response);

        } catch (Exception e) {
            log.error("Api admin get company by id error={}, cause={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Api admin get all company fail");
        }

    }

    @PatchMapping("/admin/{companyId}")
    public ResponseData<?> adminUpdateCompanyById(@PathVariable @Min(1) Long companyId,
                                                  @RequestBody @Valid CompanyRequest request) {
        log.info("Api admin update company id={}", companyId);

        try {
            CompanyResponse response = companyService.updateCompany(companyId, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Api admin update company successfully", response);
        } catch (Exception e) {
            log.error("Api admin update company error={}, cause={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Api admin update company fail");
        }
    }

    @DeleteMapping("/admin/{companyId}")
    public ResponseData<?> adminDeleteCompanyById(@PathVariable @Min(1) Long companyId) {
        log.info("Api admin delete company id={}", companyId);

        try {
            companyService.deleteCompany(companyId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Api admin delete company successfully");
        } catch (Exception e) {
            log.error("Api admin delete company error={}, cause={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Api admin delete company fail");
        }
    }

    @GetMapping("/admin/getAll")
    public ResponseData<?> adminGetListCompanies(@RequestParam @Min(0) int page,
                                                 @RequestParam @Min(1) int size) {
        log.info("Api admin get list companies");

        try {
            PageResponse<?> response = companyService.getPageCompany(page, size);
            return new ResponseData<>(HttpStatus.OK.value(), "Api admin get list companies successfully", response);
        } catch (Exception e) {
            log.error("Api admin get list companies error={}, cause={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Api admin get list companies fail");
        }
    }

    @GetMapping("/admin/search")
    public ResponseData<?> searchCompanies(@RequestParam String keyword,
                                           @RequestParam @Min(0) int page,
                                           @RequestParam @Min(1) int size) {
        log.info("Api admin search companies with keyword='{}', page={}, size={}", keyword, page, size);

        try {
            PageResponse<?> response = companyService.searchCompanies(keyword, page, size);
            return new ResponseData<>(HttpStatus.OK.value(), "Api admin search companies successfully", response);
        } catch (Exception e) {
            log.error("Api admin search companies error={}, cause={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Api admin search companies fail");
        }
    }

    @PatchMapping("/admin/profile/{companyId}")
    public ResponseData<?> updateCompanyProfile(@PathVariable @Min(1) Long companyId,
                                                @RequestBody @Valid CompanyRequest request) {
        log.info("Api admin update company profile, id={}", companyId);

        try {
            CompanyResponse response = companyService.updateCompany(companyId, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Api admin update company profile successfully", response);
        } catch (Exception e) {
            log.error("Api admin update company profile error={}, cause={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Api admin update company profile fail");
        }
    }
}
