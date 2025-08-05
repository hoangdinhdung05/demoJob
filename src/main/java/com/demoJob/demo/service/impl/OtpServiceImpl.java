package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.request.VerifyOtpRequest;
import com.demoJob.demo.entity.OtpCode;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.exception.InvalidOtpException;
import com.demoJob.demo.repository.OtpCodeRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.service.MailService;
import com.demoJob.demo.service.OtpRedisService;
import com.demoJob.demo.service.OtpService;
import com.demoJob.demo.util.OtpType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpCodeRepository otpRepo;
    private final UserRepository userRepo;
    private final MailService mailService;

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int OTP_RESEND_LIMIT_MINUTES = 5;
    private static final int MAX_OTP_SEND_COUNT = 5;

    /**
     * Gửi OTP cho người dùng.
     * Kiểm tra xem OTP đã tồn tại hay chưa, nếu có thì không gửi lại.
     * Kiểm tra số lần gửi gần đây, nếu vượt quá giới hạn thì báo lỗi.
     * Tạo OTP mới và lưu vào cơ sở dữ liệu, sau đó gửi email.
     *
     * @param user Người dùng cần gửi OTP
     * @param request Thông tin yêu cầu gửi OTP
     */
    @Override
    public void sendOtp(User user, SendOtpRequest request) {
        long userId = user.getId();
        OtpType type = request.getType();

        // Check OTP tồn tại
        if (otpRepo.findValidOtp(userId, type, LocalDateTime.now()).isPresent()) {
            throw new InvalidOtpException("OTP đã được gửi. Vui lòng kiểm tra email.");
        }

        // Check số lần gửi gần đây
        int count = otpRepo.countRecentOtpByUserAndType(
                userId, type, LocalDateTime.now().minusMinutes(OTP_RESEND_LIMIT_MINUTES));
        if (count >= MAX_OTP_SEND_COUNT) {
            throw new InvalidOtpException("Bạn đã gửi OTP quá nhiều lần. Thử lại sau.");
        }

        // Tạo OTP
        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        otpRepo.save(OtpCode.builder()
                .user(user)
                .code(otp)
                .type(type)
                .expiryTime(expiry)
                .used(false)
                .build());

        // Gửi mail
        mailService.sendOtpMail(user.getEmail(), otp, type);

        log.info("Sent OTP {} for {} to {}", otp, type, user.getEmail());
    }

    /**
     * Xác minh mã OTP.
     * Kiểm tra xem mã OTP có hợp lệ và chưa sử dụng hay không.
     * Nếu hợp lệ, đánh dấu là đã sử dụng và trả về verifyKey.
     *
     * @param request Thông tin yêu cầu xác minh OTP
     * @return verifyKey nếu xác minh thành công
     */
    @Override
    public String verifyOtp(VerifyOtpRequest request) {
        User user = userRepo.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new InvalidDataException("Email không tồn tại"));

        OtpCode otp = otpRepo.findByUserIdAndCodeAndTypeAndUsedIsFalse(
                user.getId(), request.getCode(), request.getType()
        ).orElseThrow(() -> new InvalidDataException("OTP không hợp lệ hoặc đã hết hạn"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now()))
            throw new InvalidDataException("OTP đã hết hạn");

        otp.setUsed(true);
        String verifyKey = UUID.randomUUID().toString();
        otp.setVerifyKey(verifyKey);
        otpRepo.save(otp);

        log.info("Verified OTP, key={}, user={}", verifyKey, user.getId());

        return verifyKey;
    }

    /**
     * Xác minh verifyKey để lấy thông tin người dùng.
     * @param verifyKey mã xác minh được gửi qua OTP
     * @param type loại OTP (đăng ký, đăng nhập, v.v.)
     * @return User nếu xác minh thành công
     */
    @Override
    public User confirmVerifyKey(String verifyKey, OtpType type) {
        OtpCode otp = otpRepo.findByVerifyKeyAndTypeAndUsedTrue(verifyKey, type)
                .orElseThrow(() -> new InvalidDataException("Key không hợp lệ hoặc đã hết hạn"));

        User user = otp.getUser();

        //Nếu là xác minh email thì cập nhật trạng thái emailVerified
        if (type == OtpType.VERIFY_EMAIL && Boolean.FALSE.equals(user.getEmailVerified())) {
            user.setEmailVerified(true);
            userRepo.save(user);
            log.info("[OTP] Email verified for user: {}", user.getEmail());
        }

        // Clear verifyKey
        otp.setVerifyKey(null);
        otpRepo.save(otp);

        log.info("Confirmed verify key={}, user={}", verifyKey, user.getId());

        return user;
    }

}
