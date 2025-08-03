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
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpCodeRepository otpRepo;
    private final UserRepository userRepo;
    private final MailService mailService;
    private final OtpRedisService otpRedisService;

    private static final int OTP_EXPIRY_MINUTES = 1;


    /**
     * Gửi OTP cho người dùng.
     * Nếu OTP đã được gửi trong vòng 5 phút, sẽ ném ra InvalidOtpException.
     * Nếu số lần gửi OTP vượt quá 5 lần trong vòng 5 phút, sẽ ném ra InvalidOtpException.
     *
     * @param user Người dùng nhận OTP
     * @param request Thông tin yêu cầu gửi OTP
     */
    @Override
    public void sendOtp(User user, SendOtpRequest request) {

        String email = user.getEmail();
        if (otpRedisService.getOtp(email, request.getType()) != null) {
            throw new InvalidOtpException("OTP đã được gửi, vui lòng kiểm tra email");
        }

        LocalDateTime limitWindow = LocalDateTime.now().minusMinutes(5); // Sau 5 phút mới được gửi lại
        int count = otpRepo.countRecentOtpByUserAndType(
                user.getId(), request.getType(), limitWindow
        );

        if (count >= 5) {
            throw new InvalidOtpException("Bạn đã gửi OTP quá nhiều lần. Vui lòng thử lại sau.");
        }

        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        otpRepo.save(OtpCode.builder()
                .user(user)
                .code(otp)
                .type(request.getType())
                .expiryTime(expiry)
                .used(false)
                .build());

        mailService.sendOtpMail(email, otp, request.getType());

        log.info("Sent OTP {} for {} to {}", otp, request.getType(), email);
    }

    //Logic redis phát triển lại sau
    //Redis
//    @Override
//    public boolean verifyOtp(VerifyOtpRequest request) {
//        OtpRedisData data = otpRedisService.getOtp(request.getEmail(), request.getType());
//        if (data == null) {
//            throw new RuntimeException("OTP không tồn tại hoặc đã hết hạn");
//        }
//
//        if (!data.getCode().equals(request.getCode())) {
//            throw new RuntimeException("OTP không chính xác");
//        }
//
//        if (data.getExpiryTime().isBefore(LocalDateTime.now())) {
//            throw new RuntimeException("OTP đã hết hạn");
//        }
//
//        otpRedisService.deleteOtp(request.getEmail(), request.getType());
//
//        // Cập nhật trạng thái used = true trong DB nếu bạn dùng DB song song
//        otpRepo.findByUserEmailAndCodeAndTypeAndUsedIsFalse(
//                request.getEmail(), request.getCode(), request.getType()
//        ).ifPresent(otp -> {
//            otp.setUsed(true);
//            otpRepo.save(otp);
//        });
//
//        User user = userRepo.findByEmail(request.getEmail()).orElseThrow(()->new RuntimeException("sss"));
//        user.setEmailVerified(true);
//        userRepo.save(user);
//
//        return true;
//    }

    /**
     * Xác minh OTP.
     * Nếu OTP không tồn tại hoặc đã hết hạn, sẽ ném ra InvalidDataException.
     * Nếu OTP đã được sử dụng, sẽ ném ra InvalidDataException.
     *
     * @param request Thông tin yêu cầu xác minh OTP
     * @return true nếu xác minh thành công
     */
    @Override
    public boolean verifyOtp(VerifyOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new InvalidDataException("Email không tồn tại"));

        log.info("Verifying OTP: {}, email: {}, userId: {}, type: {}", request.getCode(), email, user.getId(), request.getType());

        OtpCode otpCode = otpRepo.findByUserIdAndCodeAndTypeAndUsedIsFalse(
                user.getId(), request.getCode(), request.getType()
        ).orElseThrow(() -> new InvalidDataException("OTP not found"));

        if (otpCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP đã hết hạn");
        }

        otpCode.setUsed(true);
        otpRepo.save(otpCode);

        if (request.getType() == OtpType.VERIFY_EMAIL) {
            user.setEmailVerified(true);
            userRepo.save(user);
        }
        return true;
    }
}
