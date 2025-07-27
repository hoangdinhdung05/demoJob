package base_webSocket_demo.cron;

import base_webSocket_demo.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 12 */5 * *") // chạy vào 12h trưa mỗi 5 ngày
    public void cleanRefreshToken() {
        int deletedCount = refreshTokenRepository.deleteAllRefreshTokens();
        log.info("Deleted {} expired blacklisted tokens", deletedCount);
    }
}
