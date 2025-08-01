package com.demoJob.demo.service;

import com.demoJob.demo.dto.OtpRedisData;
import com.demoJob.demo.util.OtpType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long EXPIRY_SECONDS = 60; // 1 phút
    private static final String OTP_KEY_PATTERN = "OTP:%s:%s"; // ví dụ: OTP:login:email

    private final ObjectMapper safeMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public void saveOtp(String email, OtpRedisData data) {
        String key = buildKey(email, data.getType());
        redisTemplate.opsForValue().set(key, data, EXPIRY_SECONDS, TimeUnit.SECONDS);
        log.info("Saved OTP to Redis with key {}", key);
    }

    public OtpRedisData getOtp(String email, OtpType type) {
        String key = buildKey(email, type);
        Object raw = redisTemplate.opsForValue().get(key);
        if (raw == null) return null;

        if (raw instanceof OtpRedisData) {
            return (OtpRedisData) raw;
        }

        // Fix lỗi Devtools gây ClassLoader conflict
        return safeMapper.convertValue(raw, OtpRedisData.class);
    }

    public void deleteOtp(String email, OtpType type) {
        String key = buildKey(email, type);
        redisTemplate.delete(key);
        log.info("Deleted OTP from Redis with key {}", key);
    }

    public void setRateLimit(String email, int seconds) {
        String key = buildRateLimitKey(email);
        redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(seconds));
    }

    public boolean isRateLimited(String email) {
        String key = buildRateLimitKey(email);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private String buildKey(String email, OtpType type) {
        return String.format(OTP_KEY_PATTERN, type.name().toLowerCase(), email);
    }

    private String buildRateLimitKey(String email) {
        return "OTP:limit:" + email;
    }
}
