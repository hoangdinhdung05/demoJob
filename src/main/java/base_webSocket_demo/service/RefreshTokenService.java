package base_webSocket_demo.service;

import base_webSocket_demo.entity.RefreshToken;
import base_webSocket_demo.entity.User;

import java.util.Date;
import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user, String token, Date expiryInMs);

    Optional<RefreshToken> findByToken(String token);

    boolean isValid(RefreshToken refreshToken);

    void revokeToken(String token);

    void revokeTokenByUser(User user);

}
