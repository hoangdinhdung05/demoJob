package com.demoJob.demo.cron;

import com.demoJob.demo.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class BlacklistCleanupScheduler {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Scheduled(cron = "0 0 12 */5 * *") // chạy vào 12h trưa mỗi 5 ngày
    public void cleanupExpiredTokens() {
        int deletedCount = blacklistedTokenRepository.deleteAllExpiredTokens();
        log.info("Deleted {} expired blacklisted tokens", deletedCount);
    }

}
