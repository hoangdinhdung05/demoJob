package com.demoJob.demo.repository;

import com.demoJob.demo.entity.OtpCode;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.util.OtpType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    Optional<OtpCode> findByUserIdAndCodeAndTypeAndUsedIsFalse(Long userId, String code, OtpType type);

    @Query("""
    SELECT COUNT(o)
    FROM OtpCode o
    WHERE o.user.id = :userId
    AND o.type = :type
    AND o.createdAt >= :fromTime""")
    int countRecentOtpByUserAndType(
            @Param("userId") Long userId,
            @Param("type") OtpType type,
            @Param("fromTime") LocalDateTime fromTime
    );


}
