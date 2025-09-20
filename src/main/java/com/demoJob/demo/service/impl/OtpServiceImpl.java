package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.MailDTO.EmailDTO;
import com.demoJob.demo.dto.request.ResendOtpRequest;
import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.request.VerifyOtpRequest;
import com.demoJob.demo.entity.OtpCode;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.*;
import com.demoJob.demo.repository.OtpCodeRepository;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.service.EmailService;
import com.demoJob.demo.service.OtpService;
import com.demoJob.demo.util.enums.OtpType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpCodeRepository otpRepo;
    private final UserRepository userRepo;
    private final EmailService emailService;

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int VERIFY_KEY_EXPIRY_MINUTES = 5;
    private static final int OTP_RESEND_LIMIT_MINUTES = 5;
    private static final int MAX_OTP_SEND_COUNT = 5;

    /**
     * Gửi OTP cho người dùng.
     * Kiểm tra xem OTP đã tồn tại hay chưa, nếu có thì không gửi lại.
     * Kiểm tra số lần gửi gần đây, nếu vượt quá giới hạn thì báo lỗi.
     * Tạo OTP mới và lưu vào cơ sở dữ liệu, sau đó gửi email.
     *
     * @param request Thông tin yêu cầu gửi OTP
     * @param type  loại OTP (RESET_PASSWORD, VERIFY_EMAIL)
     */
    @Override
    public void sendOtp(SendOtpRequest request, OtpType type) {
        User user = getUser(request.getEmail());

        // Rule riêng của send lần đầu
        checkExistsAndCountSend(user.getId(), type);

        // Gửi OTP chung
        sendOtpCommon(user, type, "Mã OTP xác thực của bạn");
    }

    /**
     * Gửi OTP đến người dùng
     *
     * @param request Thông tin yêu cầu gửi OTP
     */
    @Override
    public void resendOtp(ResendOtpRequest request) {
        User user = getUser(request.getEmail());

        // Rule riêng của resend
        checkResend(user, request.getType());

        // Gửi OTP chung
        sendOtpCommon(user, request.getType(), "Mã OTP xác thực của bạn (Resend)");
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

        OtpCode otp = validateOtp(request.getEmail(), request.getCode(), OtpType.RESET_PASSWORD);
        String verifyKey = UUID.randomUUID().toString();
        otp.setVerifyKey(verifyKey);
        otp.setVerifyExpiryTime(LocalDateTime.now().plusMinutes(VERIFY_KEY_EXPIRY_MINUTES));
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
        validateOtp(request.getEmail(), request.getCode(), OtpType.VERIFY_EMAIL);

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
        OtpCode otp = validVerifyKey(verifyKey);

        User user = otp.getUser();

        // Clear verifyKey
        otp.setVerifyExpiryTime(LocalDateTime.now());
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
    private OtpCode validateOtp(String email, String code, OtpType expectedType) {
        User user = getUser(email);

        if (code.length() != 6) {
            throw new BadRequestException("OTP is not in correct format");
        }

        OtpCode otp = findAndCheckExpiryTime(code, user);

        if (otp.getType() != expectedType) {
            throw new BadRequestException("OTP type mismatch. Expected=" + expectedType);
        }

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
                .orElseThrow(() -> new NotFoundException("Email does not exist"));
    }

    /**
     * Tạo mã OTP mới và lưu vào cơ sở dữ liệu.
     * @param user Người dùng nhận OTP
     * @return Mã OTP được tạo
     */
    private String createOtpAndSaveDb(User user, OtpType type) {
        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        otpRepo.save(OtpCode.builder()
                .user(user)
                .code(otp)
                .type(type)
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
    private void checkExistsAndCountSend(long userId, OtpType type) {
        if (otpRepo.findValidOtp(userId, LocalDateTime.now()).isPresent()) {
            throw new InvalidOtpException("OTP has been sent. Please check your email.");
        }

        // Check số lần gửi gần đây
        int count = otpRepo.countRecentOtpByUser(
                userId, LocalDateTime.now().minusMinutes(OTP_RESEND_LIMIT_MINUTES), type);
        if (count >= MAX_OTP_SEND_COUNT) {
            throw new InvalidOtpException("You have sent OTP too many times. Try again later.");
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
                .orElseThrow(() -> new BadRequestException("OTP invalid or expired key"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired");
        }
        return otp;
    }

    private String buildEmailContent(User user, String otp, OtpType type) {
        String action = (type == OtpType.RESET_PASSWORD) ? "đặt lại mật khẩu" : "xác minh email";
        return "Xin chào " + user.getUsername() + ",\n\n"
                + "Mã OTP của bạn là: " + otp + "\n"
                + "Có hiệu lực trong: " + OTP_EXPIRY_MINUTES + " phút\n\n"
                + "OTP này được dùng để " + action + ".\n"
                + "Vui lòng không chia sẻ mã này cho bất kỳ ai.";
    }

    private OtpCode validVerifyKey(String verifyKey) {
        OtpCode otp = otpRepo.findByVerifyKeyAndAndUsedTrue(verifyKey)
                .orElseThrow(() -> new InvalidDataException("Key invalid or expired key"));

        if (otp.getExpiryTime() != null && otp.getVerifyExpiryTime().isBefore(LocalDateTime.now())) {
            throw new InvalidDataException("Key has expired");
        }
        return otp;
    }

    /**
     * Logic send OTP chung
     */
    private void sendOtpCommon(User user, OtpType type, String subject) {
        String otp = createOtpAndSaveDb(user, type);

        EmailDTO email = EmailDTO.builder()
                .to(List.of(user.getEmail()))
                .subject(subject)
                .textContent(buildEmailContent(user, otp, type))
                .isHtml(false)
                .build();

        emailService.sendEmailAsync(email);
        log.info("Sent OTP to {} and type {}", user.getEmail(), type);
    }

    /**
     * Validate các case về resend
     * Case 1: OTP còn hạn và chưa dùng → không cần gửi mới.
     * Case 2: Vừa gửi OTP trong thời gian OTP_RESEND_LIMIT_MINUTES → chặn spam.
     * Case 3: Nếu DB có nhiều OTP “mới nhất” cùng lúc (spam lỗi DB) → coi như spam và chặn luôn.
     */
    private void checkResend(User user, OtpType type) {
        // Lấy OTP mới nhất
        otpRepo.findFirstByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), type)
                .ifPresent(latest -> {
                    // OTP vẫn còn hạn và chưa dùng
                    if (latest.getExpiryTime().isAfter(LocalDateTime.now()) && !latest.isUsed()) {
                        throw new InvalidOtpException("Current OTP is still valid, no need to resend");
                    }

                    // Vừa request gần đây
                    if (latest.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(OTP_RESEND_LIMIT_MINUTES))) {
                        throw new InvalidOtpException("You just requested OTP, please try again in a few minutes");
                    }
                });

        // Đếm số lượng OTP đã gửi gần đây
        int count = otpRepo.countRecentOtpByUser(
                user.getId(),
                LocalDateTime.now().minusMinutes(OTP_RESEND_LIMIT_MINUTES),
                type
        );

        if (count >= MAX_OTP_SEND_COUNT) {
            throw new InvalidOtpException("You have resent OTP too many times. Try again later.");
        }
    }
}
