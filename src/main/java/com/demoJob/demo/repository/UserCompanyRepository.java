package com.demoJob.demo.repository;

import com.demoJob.demo.entity.UserCompany;
import com.demoJob.demo.util.enums.UserCompanyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserCompanyRepository extends JpaRepository<UserCompany, Long> {

    /**
     * Tìm theo companyId, owner và status
     * @param companyId id company
     * @param status trang thái
     * @return trả về thông tin userCompany
     */
    Optional<UserCompany> findByCompanyIdAndIsOwnerTrueAndStatus(Long companyId, UserCompanyStatus status);

    /**
     * Tìm theo userId, owner và status
     * @param userId id user
     * @param status trang thái
     * @return trả về thông tin userCompany
     */
    Optional<UserCompany> findByUserIdAndIsOwnerTrueAndStatus(Long userId, UserCompanyStatus status);

    /**
     * Check user có phải owner của bất kỳ company nào không
     */
    boolean existsByUserIdAndIsOwnerTrueAndStatus(Long userId, UserCompanyStatus status);

    /**
     * Check user có phải owner của 1 company cụ thể không
     */
    boolean existsByUserIdAndCompanyIdAndIsOwnerTrueAndStatus(Long userId, Long companyId, UserCompanyStatus status);

}
