package com.demoJob.demo.repository;

import com.demoJob.demo.entity.Token;
import com.demoJob.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    void deleteByToken(String token);

    void deleteAllByUserId(Long userId);

    List<Token> findAllByUserAndRevokedFalse(User user);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiryDate < CURRENT_TIMESTAMP")
    int deleteAllRefreshTokens();

    /**
     * Query token theo username
     */
    Optional<Token> findByUsername(String username);

}
