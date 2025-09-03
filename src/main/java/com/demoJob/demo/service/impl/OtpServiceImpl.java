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
import com.demoJob.demo.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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
     * @param request Thông tin yêu cầu gửi OTP
     */
    @Override
    public void sendOtp(SendOtpRequest request) {

        User user = userRepo.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new InvalidDataException("Email không tồn tại"));

        long userId = user.getId();

        // Check OTP tồn tại và số lần gửi
        checkExistsAndCountSend(userId);

        // Tạo OTP
        String otp = createOtpAndSaveDb(user);

        // Gửi mail
        mailService.sendOtpMail(user.getEmail(), otp);

        log.info("Sent OTP {} to {}", otp, user.getEmail());
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
        User user = getUser(request.getEmail());

        OtpCode otp = validateOtp(request.getEmail(), request.getCode());
        String verifyKey = UUID.randomUUID().toString();
        otp.setVerifyKey(verifyKey);
        otpRepo.save(otp);

        log.info("Verified OTP, key={}, user={}", verifyKey, user.getId());

        return verifyKey;
    }

    /**
     * Xác minh email người dùng.
     * Kiểm tra xem email đã được xác minh hay chưa.
     * Nếu chưa, xác minh email và cập nhật trạng thái.
     * Dùng cho đăng kí lần đầu => chưa vần verify key
     * @param request Thông tin xác minh bao gồm email và mã OTP
     */
    @Override
    public void verifyEmail(VerifyOtpRequest request) {
        validateOtp(request.getEmail(), request.getCode());

        User user = getUser(request.getEmail());

        user.setEmailVerified(true);
        userRepo.save(user);

        log.info("User {} đã xác minh email thành công", user.getEmail());
    }

    /**
     * Xác minh verifyKey để lấy thông tin người dùng.
     *
     * @param verifyKey mã xác minh được gửi qua OTP
     * @return User nếu xác minh thành công
     */
    @Override
    public User confirmVerifyKey(String verifyKey) {
        OtpCode otp = otpRepo.findByVerifyKeyAndUsedTrue(verifyKey)
                .orElseThrow(() -> new InvalidDataException("Key không hợp lệ hoặc đã hết hạn"));

        User user = otp.getUser();

        // Clear verifyKey
        otp.setVerifyKey(null);
        otpRepo.save(otp);

        log.info("Confirmed verify key={}, user={}", verifyKey, user.getId());

        return user;
    }

    //========== PRIVATE METHODS ==========//

    /**
     * Xác minh mã OTP và đánh dấu là đã sử dụng.
     * @param email Email của người dùng
     * @param code Mã OTP cần xác minh
     * @return OtpCode nếu xác minh thành công
     */
    private OtpCode validateOtp(String email, String code) {
        User user = getUser(email);

        OtpCode otp = findAndCheckExpiryTime(code, user);

        otp.setUsed(true);
        otpRepo.save(otp);
        return otp;
    }

    /**
     * Lấy thông tin người dùng theo email.
     * @param email Email của người dùng
     * @return User nếu tìm thấy
     */
    private User getUser(String email) {
        return userRepo.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new InvalidDataException("Email không tồn tại"));
    }

    /**
     * Tạo mã OTP mới và lưu vào cơ sở dữ liệu.
     * @param user Người dùng nhận OTP
     * @return Mã OTP được tạo
     */
    private String createOtpAndSaveDb(User user) {
        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        otpRepo.save(OtpCode.builder()
                .user(user)
                .code(otp)
                .expiryTime(expiry)
                .used(false)
                .build());
        return otp;
    }

    /**
     * Kiểm tra xem OTP đã tồn tại hay chưa và đếm số lần gửi gần đây.
     * Nếu đã gửi OTP trong thời gian giới hạn hoặc vượt quá số lần gửi, ném ngoại lệ.
     * @param userId ID của người dùng
     */
    private void checkExistsAndCountSend(long userId) {
        if (otpRepo.findValidOtp(userId, LocalDateTime.now()).isPresent()) {
            throw new InvalidOtpException("OTP đã được gửi. Vui lòng kiểm tra email.");
        }

        // Check số lần gửi gần đây
        int count = otpRepo.countRecentOtpByUser(
                userId, LocalDateTime.now().minusMinutes(OTP_RESEND_LIMIT_MINUTES));
        if (count >= MAX_OTP_SEND_COUNT) {
            throw new InvalidOtpException("Bạn đã gửi OTP quá nhiều lần. Thử lại sau.");
        }
    }

    /**
     * Tìm mã OTP và kiểm tra thời gian hết hạn.
     * @param code Mã OTP cần tìm
     * @param user Người dùng liên quan đến OTP
     * @return OtpCode nếu tìm thấy và chưa hết hạn
     */
    private OtpCode findAndCheckExpiryTime(String code, User user) {
        OtpCode otp = otpRepo.findByUserIdAndCodeAndUsedIsFalse(user.getId(), code)
                .orElseThrow(() -> new InvalidDataException("OTP không hợp lệ hoặc đã hết hạn"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new InvalidDataException("OTP đã hết hạn");
        }
        return otp;
    }
}
