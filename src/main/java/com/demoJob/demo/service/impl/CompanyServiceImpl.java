package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.request.Company.CompanyRequest;
import com.demoJob.demo.dto.request.Company.CompanyUpdateRequest;
import com.demoJob.demo.dto.response.Company.CompanyDetailResponse;
import com.demoJob.demo.dto.response.Company.CompanyResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.CompanyProfile;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserCompany;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.exception.NotFoundException;
import com.demoJob.demo.mapper.CompanyMapper;
import com.demoJob.demo.repository.CompanyProfileRepository;
import com.demoJob.demo.repository.CompanyRepository;
import com.demoJob.demo.repository.UserCompanyRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.SecurityUtils;
import com.demoJob.demo.service.CompanyService;
import com.demoJob.demo.service.MailService;
import com.demoJob.demo.util.UserCompanyUtil;
import com.demoJob.demo.util.enums.CompanyStatus;
import com.demoJob.demo.util.enums.UserCompanyStatus;
import com.demoJob.demo.validator.CompanyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import static com.demoJob.demo.mapper.CompanyMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final UserRepository userRepository;
    private final UserCompanyRepository userCompanyRepository;
    private final MailService mailService;
    private final CompanyValidator companyValidator;
    private final UserCompanyUtil userCompanyUtil;

    /**
     * HR(User) và Admin tạo ra company
     *
     * @param request thông tin company
     * @return trả về thông tin sau khi đã tạo
     */
    @Override
    public CompanyResponse createCompany(CompanyRequest request) {

        //Validate unique
        companyValidator.validateUniqueName(request.getName(), null);
        companyValidator.validateUniqueEmail(request.getEmail(), null);
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");

        //create
        Company company = buildCompany(request, isAdmin);

        //create owner
        createOwner(getCurrentUser(), company, isAdmin);

        //check role and sendmail
        if (!isAdmin) {
            mailService.sendCompanyRegistrationNotification(company, getCurrentUser());
        }

        log.info("Company created by {} with id={} status={}",
                isAdmin ? "Admin" : "User", company.getId(), company.getStatus());

        return toResponse(company);
    }

    /**
     * Dùng cho user có quyền update thông tin company
     *
     * @param request thông tin cần update
     * @return trả về thông tin sau update
     */
    @Override
    public CompanyDetailResponse updateCompany(CompanyUpdateRequest request) {
        Company company = getCompanyByIdOrThrow(request.getId());
        User currentUser = getCurrentUser();
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");

        // Check quyền
        if (!isAdmin && !userCompanyUtil.isOwnerOfCompany(currentUser, company)) {
            throw new InvalidDataException("Bạn không có quyền update company");
        }

        // Validate + Apply update
        companyValidator.validateUniqueName(request.getName(), company.getName());
        companyValidator.validateUniqueEmail(request.getEmail(), company.getEmail());
        updateCompanyRequest(request, company, isAdmin);

        // Save (cascade profile nên không cần save profile riêng)
        companyRepository.save(company);

        log.info("Company updated by {} (userId={}) with companyId={}",
                isAdmin ? "Admin" : "Owner", currentUser.getId(), company.getId());

        return toDetailsResponse(company);
    }


    /**
     * Hiển thị thông tin cơ bản trên bảng tin cho ứng viên xem
     *
     * @param companyId id company hiển thị
     * @return trả về thông tin cơ bản
     */
    @Override
    public CompanyResponse getCompanyById(Long companyId) {
        Company company = checkActiveCompany(companyId);
        log.info("Get info company successfully with id={}", companyId);
        return toResponse(company);
    }

    /**
     * Xem thông tin chi tiết của company đó
     *
     * @param companyId id company hiển thị
     * @return trả về thông tin chi tiết
     */
    @Override
    public CompanyDetailResponse getDetailsCompany(Long companyId) {
        Company company = checkActiveCompany(companyId);
        log.info("Get details company successfully with id={}", companyId);
        return toDetailsResponse(company);
    }

    /**
     * Ứng viên xem danh sách companies
     *
     * @param page Trang
     * @param size kích thước
     * @return trả về thông tin có phân trang
     */
    @Override
    public PageResponse<?> getAllCompanies(int page, int size) {
        Page<Company> companyPage;

        if (SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("MANAGER")) {
            companyPage = companyRepository.findAll(PageRequest.of(page, size));
        } else {
            companyPage = companyRepository.findByStatus(CompanyStatus.ACTIVE, PageRequest.of(page, size));
        }

        List<CompanyResponse> responseList = companyPage.stream()
                .map(CompanyMapper::toResponse)
                .toList();

        log.info("Get alls company successfully");

        return PageResponse.<CompanyResponse>builder()
                .page(companyPage.getNumber())
                .size(companyPage.getSize())
                .total(companyPage.getTotalElements())
                .items(responseList)
                .build();
    }

    /**
     * Dùng cho việc update status company
     *
     * @param companyId id company cần update
     * @param newStatus status cần đổi
     * @param reason    lí do từ chối
     */
    @Override
    public void updateCompanyStatus(Long companyId, CompanyStatus newStatus, String reason) {
        Company company = getCompanyByIdOrThrow(companyId);

        boolean validTransition = switch (company.getStatus()) {
            case PENDING -> (newStatus == CompanyStatus.ACTIVE || newStatus == CompanyStatus.REJECTED);
            case ACTIVE -> (newStatus == CompanyStatus.PENDING);
            default -> false;
        };

        if (!validTransition) {
            throw new InvalidDataException(
                    String.format("Cannot change company status from %s to %s", company.getStatus(), newStatus)
            );
        }

        company.setStatus(newStatus);
        companyRepository.save(company);

        UserCompany owner = userCompanyUtil.getOwnerCompany(companyId);

        // Gửi mail theo trạng thái
        switch (newStatus) {
            case ACTIVE -> mailService.sendCompanyApprovalNotification(company, owner.getUser());
            case REJECTED -> mailService.sendCompanyRejectionNotification(company, owner.getUser(), reason);
            case PENDING -> mailService.sendCompanyBackToPendingNotification(company, owner.getUser());
            default -> log.warn("Unhandled company status update: {}", newStatus);
        }

        log.info("Company {} updated to {} with id={}", companyId, newStatus, companyId);
    }

    /**
     * Admin xóa company
     *
     * @param companyId id company
     */
    @Override
    public void deleteCompany(Long companyId) {
        Company company = getCompanyByIdOrThrow(companyId);

        if (!SecurityUtils.hasRole("ADMIN") && !SecurityUtils.hasRole("MANAGER")) {
            throw new InvalidDataException("Bạn không đủ quyền hạn");
        }

        company.setStatus(CompanyStatus.DELETED);
        companyRepository.save(company);

        log.info("Company soft-deleted by {} with id={}", getCurrentUser().getId(), companyId);
    }


    //========== PRIVATE METHOD ==========//

    private Company getCompanyByIdOrThrow(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Company not found with id: " + id));
    }

    private Company buildCompany(CompanyRequest request, boolean isAdmin) {
        Company company = Company.builder()
                .name(request.getName())
                .email(request.getEmail())
                .logo(request.getLogo())
                .phone(request.getPhone())
                .website(request.getWebsite())
                .status(isAdmin ? CompanyStatus.ACTIVE : CompanyStatus.PENDING)
                .build();

        company = companyRepository.save(company);

        //create profile
        CompanyProfile profile = CompanyProfile.builder().company(company).build();
        companyProfileRepository.save(profile);
        company.setProfile(profile);

        return company;
    }

    private User getCurrentUser() {
        try {
            long userId = SecurityUtils.getCurrentUserId();
            return userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
        } catch (Exception e) {
            throw new SecurityException("Invalid authentication token", e);
        }
    }

    private void createOwner(User user, Company company, boolean isAdmin) {
        UserCompany userCompany = UserCompany.builder()
                .user(user)
                .company(company)
                .position(isAdmin ? "ADMIN" : "HR")
                .isOwner(true)
                .status(UserCompanyStatus.ACTIVE)
                .startDate(LocalDate.now())
                .build();

        userCompanyRepository.save(userCompany);
        log.info("Create owner for user: {} and company: {}", user.getId(), company.getName());
    }

    private Company checkActiveCompany(Long companyId) {
        Company company = getCompanyByIdOrThrow(companyId);

        //User
        if (company.getStatus() == CompanyStatus.ACTIVE) return company;

        //Admin and manager
        if (SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("MANAGER")) return company;

        //check pending => user create company vẫn xem được
        User user = SecurityUtils.getCurrentUserDetails().getUser();
        if (company.getStatus() == CompanyStatus.PENDING && userCompanyUtil.isOwnerOfCompany(user, company)) return company;

        throw new NotFoundException("Company not found with id: " + companyId);
    }
}
