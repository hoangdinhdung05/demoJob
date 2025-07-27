package base_webSocket_demo.repository;

import base_webSocket_demo.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByToken(String token);

    @Modifying
    @Query("DELETE FROM BlackListedToken b WHERE b.expiryDate < CURRENT_TIMESTAMP")
    int deleteAllExpiredTokens();
}
