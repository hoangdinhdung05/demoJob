package com.demoJob.demo.repository;

import com.demoJob.demo.entity.OtpCode;
import com.demoJob.demo.util.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByUserEmailAndCodeAndTypeAndUsedIsFalse(String email, String code, OtpType type);
}
