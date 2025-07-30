package base_webSocket_demo.service.impl;

import base_webSocket_demo.dto.OtpRedisData;
import base_webSocket_demo.dto.request.SendOtpRequest;
import base_webSocket_demo.dto.response.VerifyOtpRequest;
import base_webSocket_demo.entity.OtpCode;
import base_webSocket_demo.entity.User;
import base_webSocket_demo.repository.OtpCodeRepository;
import base_webSocket_demo.repository.UserRepository;
import base_webSocket_demo.service.MailService;
import base_webSocket_demo.service.OtpRedisService;
import base_webSocket_demo.service.OtpService;
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

    @Override
    public void sendOtp(SendOtpRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String email = user.getEmail();
        if (otpRedisService.getOtp(email, request.getType()) != null) {
            throw new RuntimeException("OTP đã được gửi, vui lòng kiểm tra email");
        }

        if (otpRedisService.isRateLimited(email)) {
            throw new RuntimeException("Bạn đang gửi OTP quá nhiều lần. Vui lòng đợi trong giây lát");
        }

        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        // Lưu vào MySQL để tracking (không bắt buộc nếu dùng Redis 100%)
        otpRepo.save(OtpCode.builder()
                .user(user)
                .code(otp)
                .type(request.getType())
                .expiryTime(expiry)
                .used(false)
                .build());

        otpRedisService.saveOtp(email, OtpRedisData.builder()
                .userId(user.getId())
                .code(otp)
                .type(request.getType())
                .expiryTime(expiry)
                .build());

        otpRedisService.setRateLimit(email, 60);

        mailService.sendOtpMail(email, otp, request.getType());
        log.info("Sent OTP {} for {} to {}", otp, request.getType(), email);
    }

    @Override
    public boolean verifyOtp(VerifyOtpRequest request) {
        OtpRedisData data = otpRedisService.getOtp(request.getEmail(), request.getType());
        if (data == null) {
            throw new RuntimeException("OTP không tồn tại hoặc đã hết hạn");
        }

        if (!data.getCode().equals(request.getCode())) {
            throw new RuntimeException("OTP không chính xác");
        }

        if (data.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP đã hết hạn");
        }

        otpRedisService.deleteOtp(request.getEmail(), request.getType());

        // Cập nhật trạng thái used = true trong DB nếu bạn dùng DB song song
        otpRepo.findByUserEmailAndCodeAndTypeAndUsedIsFalse(
                request.getEmail(), request.getCode(), request.getType()
        ).ifPresent(otp -> {
            otp.setUsed(true);
            otpRepo.save(otp);
        });

        return true;
    }
}
