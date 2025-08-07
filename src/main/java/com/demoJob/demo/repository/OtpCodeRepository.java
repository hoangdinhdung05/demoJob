package com.demoJob.demo.repository;

import com.demoJob.demo.entity.OtpCode;
import com.demoJob.demo.util.OtpType;
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
     * @param type Loại OTP (ví dụ: Đăng ký, Đăng nhập, Quên mật khẩu)
     * @param now Thời gian hiện tại để kiểm tra tính hợp lệ
     * @return Optional chứa mã OTP nếu tìm thấy, hoặc rỗng nếu không có mã hợp lệ
     */
    @Query("""
            SELECT o
            FROM OtpCode o
            WHERE o.user.id = :userId
              AND o.type = :type
              AND o.expiryTime > :now
              AND o.used = false
            """)
    Optional<OtpCode> findValidOtp(@Param("userId") Long userId,
                                   @Param("type") OtpType type,
                                   @Param("now") LocalDateTime now);

    /**
     * Đếm số lượng mã OTP đã gửi gần đây cho người dùng theo ID và loại OTP.
     * @param userId ID của người dùng
     * @param type Loại OTP (ví dụ: Đăng ký, Đăng nhập, Quên mật khẩu)
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
    int countRecentOtpByUserAndType(@Param("userId") Long userId,
                                    @Param("type") OtpType type,
                                    @Param("after") LocalDateTime after);

    /**
     * Tìm mã OTP theo ID người dùng, mã OTP và loại OTP mà chưa được sử dụng.
     * @param userId ID của người dùng
     * @param code Mã OTP cần tìm
     * @param type Loại OTP (ví dụ: Đăng ký, Đăng nhập, Quên mật khẩu)
     * @return Optional chứa mã OTP nếu tìm thấy, hoặc rỗng nếu không có mã hợp lệ
     */
    Optional<OtpCode> findByUserIdAndCodeAndTypeAndUsedIsFalse(Long userId, String code, OtpType type);

    /**
     * Tìm mã OTP theo verifyKey và loại OTP mà đã được sử dụng.
     * @param verifyKey verifyKey để tìm kiếm mã OTP
     * @param type Loại OTP (ví dụ: Đăng ký, Đăng nhập, Quên mật khẩu)
     * @return Optional chứa mã OTP nếu tìm thấy, hoặc rỗng nếu không có mã hợp lệ
     */
    Optional<OtpCode> findByVerifyKeyAndTypeAndUsedTrue(String verifyKey, OtpType type);
}
