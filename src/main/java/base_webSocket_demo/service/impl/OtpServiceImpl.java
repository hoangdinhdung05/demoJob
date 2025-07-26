package base_webSocket_demo.service.impl;

import base_webSocket_demo.dto.request.SendOtpRequest;
import base_webSocket_demo.dto.response.VerifyOtpRequest;
import base_webSocket_demo.entity.OtpCode;
import base_webSocket_demo.entity.User;
import base_webSocket_demo.repository.OtpCodeRepository;
import base_webSocket_demo.repository.UserRepository;
import base_webSocket_demo.service.MailService;
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

    private static final int OTP_EXPIRY_MINUTES = 1;

    @Override
    public void sendOtp(SendOtpRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        OtpCode code = OtpCode.builder()
                .user(user)
                .code(otp)
                .type(request.getType())
                .expiryTime(expiry)
                .used(false)
                .build();
        otpRepo.save(code);

        mailService.sendOtpMail(user.getEmail(), otp, request.getType());
        log.info("Sent OTP {} for {} to {}", otp, request.getType(), user.getEmail());
    }

    @Override
    public boolean verifyOtp(VerifyOtpRequest request) {
        OtpCode otp = otpRepo.findByUserEmailAndCodeAndTypeAndUsedIsFalse(
                request.getEmail(), request.getCode(), request.getType()
        ).orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        otp.setUsed(true);
        otpRepo.save(otp);
        return true;
    }
}
