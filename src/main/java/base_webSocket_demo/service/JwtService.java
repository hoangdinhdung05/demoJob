package base_webSocket_demo.service;

import java.sql.Date;
import org.springframework.security.core.userdetails.UserDetails;
import base_webSocket_demo.util.TokenType;
import io.jsonwebtoken.Claims;

public interface JwtService {

    String generateToken(UserDetails user);

    String generateRefreshToken(UserDetails user);

    String extractUsername(String token, TokenType type);

    boolean isValid(String token, TokenType type, UserDetails user);

    Date extractExpiration(String token, TokenType type);

    Claims extractAllClaims(String token);

}
