package com.demoJob.demo.util;

import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserCompany;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.repository.UserCompanyRepository;
import com.demoJob.demo.util.enums.UserCompanyStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCompanyUtil {

    private final UserCompanyRepository userCompanyRepository;

    /**
     * Lấy thông tin owner của 1 company cụ thể
     */
    public UserCompany getOwnerCompany(Long companyId) {
        return userCompanyRepository
                .findByCompanyIdAndIsOwnerTrueAndStatus(companyId, UserCompanyStatus.ACTIVE)
                .orElseThrow(() -> new InvalidDataException("Company owner not found"));
    }

    /**
     * Check user có phải owner của 1 company cụ thể không
     */
    public boolean isOwnerOfCompany(User user, Company company) {
        if (user == null || user.getId() == null || company == null || company.getId() == null) return false;
        return userCompanyRepository
                .existsByUserIdAndCompanyIdAndIsOwnerTrueAndStatus(user.getId(), company.getId(), UserCompanyStatus.ACTIVE);
    }

    /**
     * Check user có phải owner của bất kỳ company nào không
     */
    public boolean isOwner(User user) {
        if (user == null || user.getId() == null) return false;
        return userCompanyRepository
                .existsByUserIdAndIsOwnerTrueAndStatus(user.getId(), UserCompanyStatus.ACTIVE);
    }

    /**
     * Lấy companyId mà user owner
     */
    public Long getOwnerCompanyId(User user) {
        if (user == null || user.getId() == null) {
            throw new InvalidDataException("Invalid user");
        }
        return userCompanyRepository.findByUserIdAndIsOwnerTrueAndStatus(user.getId(), UserCompanyStatus.ACTIVE)
                .map(uc -> uc.getCompany().getId())
                .orElseThrow(() -> new InvalidDataException("Owner company not found for userId = " + user.getId()));
    }

    /**
     * Lấy danh sách công ty mà user tham gia (ACTIVE)
     */
    public List<UserCompany> getCompaniesOfUser(User user) {
        if (user == null || user.getId() == null) {
            return List.of();
        }
        return userCompanyRepository.findByUserAndStatus(user, UserCompanyStatus.ACTIVE);
    }
}