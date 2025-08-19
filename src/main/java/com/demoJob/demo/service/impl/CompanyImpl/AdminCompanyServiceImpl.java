package com.demoJob.demo.service.impl.CompanyImpl;

import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.UserCompany;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.exception.NotFoundException;
import com.demoJob.demo.repository.CompanyRepository;
import com.demoJob.demo.repository.UserCompanyRepository;
import com.demoJob.demo.service.CompanyService.AdminCompanyService;
import com.demoJob.demo.service.MailService;
import com.demoJob.demo.util.enums.CompanyStatus;
import com.demoJob.demo.util.enums.UserCompanyStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCompanyServiceImpl implements AdminCompanyService {

    private final CompanyRepository companyRepository;
    private final UserCompanyRepository userCompanyRepository;
    private final MailService mailService;

    @Override
    public void updateCompanyStatus(Long companyId, CompanyStatus newStatus, String reason) {
        Company company = findCompanyByIdOrThrow(companyId);

        // Chỉ cho phép update khi đang PENDING
        if (company.getStatus() != CompanyStatus.PENDING) {
            throw new InvalidDataException("Company is not in pending status");
        }

        company.setStatus(newStatus);
        companyRepository.save(company);

        UserCompany owner = getOwnerCompany(companyId);

        // Gửi mail theo trạng thái
        switch (newStatus) {
            case ACTIVE -> mailService.sendCompanyApprovalNotification(company, owner.getUser());
            case REJECTED -> mailService.sendCompanyRejectionNotification(company, owner.getUser(), reason);
            default -> log.warn("Unhandled company status update: {}", newStatus);
        }

        log.info("Company {} updated to {} with id={}", companyId, newStatus, companyId);
    }


    //====== PRIVATE METHOD =======/


    private Company findCompanyByIdOrThrow(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company not found with id: " + companyId));
    }

    private UserCompany getOwnerCompany(Long companyId) {
        return userCompanyRepository
                .findByCompanyIdAndIsOwnerTrueAndStatus(companyId, UserCompanyStatus.ACTIVE)
                .orElseThrow(() -> new InvalidDataException("Company owner not found"));
    }
}
