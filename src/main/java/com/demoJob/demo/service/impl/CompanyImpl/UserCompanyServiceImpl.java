package com.demoJob.demo.service.impl.CompanyImpl;

import com.demoJob.demo.dto.request.Company.CompanyRequest;
import com.demoJob.demo.dto.response.Company.CompanyResponse;
import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.CompanyProfile;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserCompany;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.repository.CompanyProfileRepository;
import com.demoJob.demo.repository.CompanyRepository;
import com.demoJob.demo.repository.UserCompanyRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.SecurityUtils;
import com.demoJob.demo.service.CompanyService.UserCompanyService;
import com.demoJob.demo.service.MailService;
import com.demoJob.demo.util.enums.CompanyStatus;
import com.demoJob.demo.util.enums.UserCompanyStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import static com.demoJob.demo.mapper.CompanyMapper.toResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCompanyServiceImpl implements UserCompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final UserRepository userRepository;
    private final UserCompanyRepository userCompanyRepository;
    private final MailService mailService;

    /**
     * HR(User) tạo ra company
     *
     * @param request thông tin company
     * @return trả về thông tin sau khi đã tạo
     */
    @Override
    public CompanyResponse createCompany(CompanyRequest request) {

        //Validate unique
        validateRequest(request);
        Company company = buildCompany(request);

        log.info("Create company successfully with company id={}", company.getId());

        return toResponse(company);
    }


    //========== PRIVATE METHOD ==========//

    private void validateRequest(CompanyRequest request) {
        if (request.getName() != null && companyRepository.existsByName(request.getName())) {
            throw new InvalidDataException("Company name already exists " + request.getName());
        }

        if (request.getEmail() != null && companyRepository.existsByEmail(request.getEmail())) {
            throw new InvalidDataException("Company email already exists " + request.getEmail());
        }
    }

    private Company buildCompany(CompanyRequest request) {
        Company company = Company.builder()
                .name(request.getName())
                .email(request.getEmail())
                .logo(request.getLogo())
                .phone(request.getPhone())
                .website(request.getWebsite())
                .status(CompanyStatus.PENDING)
                .build();

        company = companyRepository.save(company);

        //tạo luôn profile
        CompanyProfile profile = CompanyProfile.builder().company(company).build();
        companyProfileRepository.save(profile);
        company.setProfile(profile);

        createOwner(getCurrentUser(), company);

        mailService.sendCompanyRegistrationNotification(company, getCurrentUser());
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

    private void createOwner(User user, Company company) {
        UserCompany userCompany = UserCompany.builder()
                .user(user)
                .company(company)
                .position("HR")
                .isOwner(true)
                .status(UserCompanyStatus.ACTIVE)
                .startDate(LocalDate.now())
                .build();

        userCompanyRepository.save(userCompany);
        log.info("Create owner for user: {} and company: {}", user.getId(), company.getName());
    }
}
