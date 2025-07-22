package base_webSocket_demo.service.impl;

import base_webSocket_demo.dto.request.Admin.Company.CompanyProfileRequest;
import base_webSocket_demo.dto.request.Admin.Company.CompanyRequest;
import base_webSocket_demo.dto.response.Admin.Company.CompanyProfileResponse;
import base_webSocket_demo.dto.response.Admin.Company.CompanyResponse;
import base_webSocket_demo.dto.response.system.PageResponse;
import base_webSocket_demo.entity.Company;
import base_webSocket_demo.entity.CompanyProfile;
import base_webSocket_demo.repository.CompanyProfileRepository;
import base_webSocket_demo.repository.CompanyRepository;
import base_webSocket_demo.service.CompanyService;
import base_webSocket_demo.util.CompanyStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyProfileRepository companyProfileRepository;

    @Override
    public CompanyResponse createCompany(CompanyRequest request) {
        Company company = Company.builder()
                .name(request.getName())
                .logo(request.getLogo())
                .email(request.getEmail())
                .phone(request.getPhone())
                .website(request.getWebsite())
                .status(request.getStatus())
                .build();

        company = companyRepository.save(company);

        CompanyProfile companyProfile = buildCompanyProfile(request, company);
        companyProfileRepository.save(companyProfile);

        company.setProfile(companyProfile);

        return convertToCompany(company);
    }

    @Override
    public CompanyResponse updateCompany(long companyId, CompanyRequest request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        company.setName(request.getName());
        company.setLogo(request.getLogo());
        company.setEmail(request.getEmail());
        company.setPhone(request.getPhone());
        company.setWebsite(request.getWebsite());
        company.setStatus(request.getStatus());

        CompanyProfile profile = company.getProfile();
        updateCompanyProfile(profile, request.getCompanyProfileRequest());

        companyRepository.save(company);
        companyProfileRepository.save(profile);

        return convertToCompany(company);
    }

    @Override
    public CompanyResponse changeCompanyStatus(long companyId, CompanyStatus status) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));
        company.setStatus(status);
        companyRepository.save(company);
        return convertToCompany(company);
    }

    @Override
    public void deleteCompany(long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));
        companyProfileRepository.delete(company.getProfile());
        companyRepository.delete(company);
    }

    @Override
    public CompanyResponse getCompanyByCompanyId(long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));
        return convertToCompany(company);
    }

    @Override
    public List<CompanyResponse> getAllCompanys(Specification<Company> specification) {
        return companyRepository.findAll(specification).stream()
                .map(this::convertToCompany)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<?> getPageCompany(int page, int size) {
        Page<Company> companyPage = companyRepository.findAll(PageRequest.of(page, size));
        List<CompanyResponse> list = companyPage.stream()
                .map(this::convertToCompany)
                .toList();

        return PageResponse.<CompanyResponse>builder()
                .page(companyPage.getNumber() + 1)
                .size(companyPage.getSize())
                .total(companyPage.getTotalElements())
                .items(list)
                .build();
    }

    @Override
    public PageResponse<?> searchCompanies(String keyword, int page, int size) {
        Page<Company> companyPage = companyRepository.findByNameContainingIgnoreCase(keyword, PageRequest.of(page, size));

        List<CompanyResponse> list = companyPage.stream()
                .map(this::convertToCompany)
                .toList();

        return PageResponse.<CompanyResponse>builder()
                .page(companyPage.getNumber() + 1)
                .size(companyPage.getSize())
                .total(companyPage.getTotalElements())
                .items(list)
                .build();
    }

    private CompanyProfile buildCompanyProfile(CompanyRequest request, Company company) {
        CompanyProfileRequest profileReq = request.getCompanyProfileRequest();
        return CompanyProfile.builder()
                .company(company)
                .description(profileReq.getDescription())
                .address(profileReq.getAddress())
                .city(profileReq.getCity())
                .country(profileReq.getCountry())
                .industry(profileReq.getIndustry())
                .taxCode(profileReq.getTaxCode())
                .companySize(profileReq.getCompanySize())
                .workingTime(profileReq.getWorkingTime())
                .culture(profileReq.getCulture())
                .benefits(profileReq.getBenefits())
                .mapLocation(profileReq.getMapLocation())
                .build();
    }

    private void updateCompanyProfile(CompanyProfile profile, CompanyProfileRequest profileReq) {
        profile.setDescription(profileReq.getDescription());
        profile.setAddress(profileReq.getAddress());
        profile.setCity(profileReq.getCity());
        profile.setCountry(profileReq.getCountry());
        profile.setIndustry(profileReq.getIndustry());
        profile.setTaxCode(profileReq.getTaxCode());
        profile.setCompanySize(profileReq.getCompanySize());
        profile.setWorkingTime(profileReq.getWorkingTime());
        profile.setCulture(profileReq.getCulture());
        profile.setBenefits(profileReq.getBenefits());
        profile.setMapLocation(profileReq.getMapLocation());
    }

    private CompanyProfileResponse convertToCompanyProfile(CompanyProfile profile) {
        return CompanyProfileResponse.builder()
                .description(profile.getDescription())
                .address(profile.getAddress())
                .city(profile.getCity())
                .country(profile.getCountry())
                .industry(profile.getIndustry())
                .taxCode(profile.getTaxCode())
                .companySize(profile.getCompanySize())
                .workingTime(profile.getWorkingTime())
                .culture(profile.getCulture())
                .benefits(profile.getBenefits())
                .mapLocation(profile.getMapLocation())
                .build();
    }

    private CompanyResponse convertToCompany(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .logo(company.getLogo())
                .email(company.getEmail())
                .phone(company.getPhone())
                .website(company.getWebsite())
                .status(company.getStatus())
                .companyProfile(convertToCompanyProfile(company.getProfile()))
                .createdBy(company.getCreatedBy())
                .updatedBy(company.getUpdatedBy())
                .build();
    }
}
