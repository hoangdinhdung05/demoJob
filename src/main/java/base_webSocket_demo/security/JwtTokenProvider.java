package base_webSocket_demo.security;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    
    @Value("${jwt.accessKey}")
    private String accessKeyBase64;

    @Value("${jwt.refreshKey}")
    private String refreshKeyBase64;

    @Value("${jwt.expiryHour}")
    private int expiryHour;

    @Value("${jwt.expiryDay}")
    private int expiryDay;

    private Key accessKey;
    private Key refreshKey;

    @PostConstruct
    public void init() {
        accessKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(accessKeyBase64));
        refreshKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(refreshKeyBase64));
    }

    //ACCESS TOKEN
    public String generateAccessToken(Authentication authentication) {
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        String authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.joining(","));

        Date now = new Date();
        long expiryMillis = now.getTime() + expiryHour * 3600_000L + expiryDay * 24 * 3600_000L;
        Date expiryDate = new Date(expiryMillis);


        return Jwts.builder()
                .setSubject(principal.getUsername())
                .claim("role", authorities)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(accessKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // log error if needed
        }
        return false;
    }

    // -- REFRESH TOKEN --

    public String generateRefreshToken(String username) {
        Date now = new Date();
        long expiryMillis = now.getTime() + expiryDay * 24 * 3600_000L;
        Date expiryDate = new Date(expiryMillis);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(refreshKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // log error if needed
        }
        return false;
    }

    public String getUsernameFromRefreshToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // -- COMMON CLAIMS --

    public String getUsernameFromAccessToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String getRolesFromAccessToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("roles");
    }

    public LocalDateTime isTokenExpired(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date expiration = claims.getExpiration();

        return expiration.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
