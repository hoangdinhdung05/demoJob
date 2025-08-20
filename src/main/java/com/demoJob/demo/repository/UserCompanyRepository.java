package com.demoJob.demo.repository;

import com.demoJob.demo.entity.UserCompany;
import com.demoJob.demo.util.enums.UserCompanyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCompanyRepository extends JpaRepository<UserCompany, Long> {

    void deleteByCompanyId(Long companyId);

    Optional<UserCompany> findByCompanyIdAndIsOwnerTrueAndStatus(Long companyId, UserCompanyStatus status);
}
