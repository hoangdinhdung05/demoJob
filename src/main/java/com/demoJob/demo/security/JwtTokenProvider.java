package com.demoJob.demo.security;

import com.demoJob.demo.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Value("${jwt.expiryMinutes}")
    private long expiryMinutes;

    @Value("${jwt.expiryDay}")
    private long expiryDay;

    private Key accessKey;
    private Key refreshKey;

    @PostConstruct
    public void init() {
        accessKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(accessKeyBase64));
        refreshKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(refreshKeyBase64));
    }

    // ===================== ACCESS TOKEN =====================

    public String generateAccessToken(Authentication authentication) {
//        var principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

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

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }


    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // hoặc getEmail()
        }

        if (principal instanceof String) {
            return (String) principal; // ví dụ: "admin"
        }

        return null;
    }

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
        long expiryMillis = System.currentTimeMillis() + 1000 * 60 * expiryMinutes;
        return new Date(expiryMillis);
    }

    public Date getRefreshTokenExpiryDate() {
        long expiryMillis = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay;
        return new Date(expiryMillis);
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
