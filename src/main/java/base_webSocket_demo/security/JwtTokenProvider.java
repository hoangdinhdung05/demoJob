package base_webSocket_demo.security;

import base_webSocket_demo.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    // ===================== ACCESS TOKEN =====================

    public String generateAccessToken(Authentication authentication) {
        var principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        String roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return buildToken(principal.getUsername(), roles, accessKey, getAccessTokenExpiryDate());
    }

    public String generateAccessToken(User user) {
        String roles = user.getUserHasRoles().stream()
                .map(role -> "ROLE_" + role.getRole().getName().toUpperCase())
                .collect(Collectors.joining(","));

        return buildToken(user.getUsername(), roles, accessKey, getAccessTokenExpiryDate());
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accessKey);
    }

    public String getUsernameFromAccessToken(String token) {
        return extractClaims(token, accessKey).getSubject();
    }

    public String getRolesFromAccessToken(String token) {
        return extractClaims(token, accessKey).get("roles", String.class);
    }

    public LocalDateTime getAccessTokenExpiry(String token) {
        return toLocalDateTime(extractClaims(token, accessKey).getExpiration());
    }

    // ===================== REFRESH TOKEN =====================

    public String generateRefreshToken(String username) {
        return buildToken(username, null, refreshKey, getRefreshTokenExpiryDate());
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshKey);
    }

    public String getUsernameFromRefreshToken(String token) {
        return extractClaims(token, refreshKey).getSubject();
    }

    // ===================== COMMON UTILS =====================

    private String buildToken(String subject, String roles, Key key, Date expiryDate) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512);

        if (roles != null) {
            builder.claim("roles", roles);
        }

        return builder.compact();
    }

    private boolean validateToken(String token, Key key) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Claims extractClaims(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date getAccessTokenExpiryDate() {
        long expiryMillis = System.currentTimeMillis() + expiryHour * 3600_000L + expiryDay * 24 * 3600_000L;
        return new Date(expiryMillis);
    }

    private Date getRefreshTokenExpiryDate() {
        long expiryMillis = System.currentTimeMillis() + expiryDay * 24 * 3600_000L;
        return new Date(expiryMillis);
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
