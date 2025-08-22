package com.demoJob.demo.util;

import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserCompany;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.repository.UserCompanyRepository;
import com.demoJob.demo.util.enums.UserCompanyStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCompanyUtil {

    private final UserCompanyRepository userCompanyRepository;

    public UserCompany getOwnerCompany(Long companyId) {
        return userCompanyRepository
                .findByCompanyIdAndIsOwnerTrueAndStatus(companyId, UserCompanyStatus.ACTIVE)
                .orElseThrow(() -> new InvalidDataException("Company owner not found"));
    }

    public boolean isOwner(User user, Company company) {
        if (user == null || user.getId() == null) return false;
        UserCompany ownerCompany = getOwnerCompany(company.getId());
        return ownerCompany.getUser().getId().equals(user.getId());
    }

}
