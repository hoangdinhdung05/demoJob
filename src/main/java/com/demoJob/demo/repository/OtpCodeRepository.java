package com.demoJob.demo.repository;

import com.demoJob.demo.entity.OtpCode;
import com.demoJob.demo.util.enums.OtpType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    /**
     * Tìm mã OTP hợp lệ cho người dùng theo ID và loại OTP.
     * @param userId ID của người dùng
     * @param now Thời gian hiện tại để kiểm tra tính hợp lệ
     * @return Optional chứa mã OTP nếu tìm thấy, hoặc rỗng nếu không có mã hợp lệ
     */
    @Query("""
            SELECT o
            FROM OtpCode o
            WHERE o.user.id = :userId
              AND o.expiryTime > :now
              AND o.used = false
            """)
    Optional<OtpCode> findValidOtp(@Param("userId") Long userId,
                                   @Param("now") LocalDateTime now);

    /**
     * Đếm số lượng mã OTP đã gửi gần đây cho người dùng theo ID và loại OTP.
     * @param userId ID của người dùng
     * @param after Thời gian sau đó để kiểm tra số lượng mã OTP đã gửi
     * @return Số lượng mã OTP đã gửi gần đây
     */
    @Query("""
            SELECT COUNT(o)
            FROM OtpCode o
            WHERE o.user.id = :userId
              AND o.type = :type
              AND o.createdAt > :after
            """)
    int countRecentOtpByUser(@Param("userId") Long userId,
                             @Param("after") LocalDateTime after,
                             @Param("type") OtpType type);

    /**
     * Tìm mã OTP theo ID người dùng, mã OTP và loại OTP mà chưa được sử dụng.
     * @param userId ID của người dùng
     * @param code Mã OTP cần tìm
     * @return Optional chứa mã OTP nếu tìm thấy, hoặc rỗng nếu không có mã hợp lệ
     */
    Optional<OtpCode> findByUserIdAndCodeAndUsedIsFalse(Long userId, String code);

    /**
     * Tìm mã OTP theo verifyKey và loại OTP mà đã được sử dụng.
     * @param verifyKey verifyKey để tìm kiếm mã OTP
     * @return Optional chứa mã OTP nếu tìm thấy, hoặc rỗng nếu không có mã hợp lệ
     */
    Optional<OtpCode> findByVerifyKeyAndAndUsedTrue(String verifyKey);

    /**
     * Lấy OTP mới nhất theo user + type
     */
    Optional<OtpCode> findFirstByUserIdAndTypeOrderByCreatedAtDesc(Long userId, OtpType type);


}
