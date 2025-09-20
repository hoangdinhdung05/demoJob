package com.demoJob.demo.security;

import java.io.IOException;
import java.util.Arrays;

import com.demoJob.demo.config.SecurityConfig;
import com.demoJob.demo.service.BlacklistService;
import com.demoJob.demo.service.TokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final BlacklistService blacklistService;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return Arrays.stream(SecurityConfig.PUBLIC_URL)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("========doFilterInternal=========");

        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);

            if (jwtTokenProvider.validateAccessToken(token)) {

                if (blacklistService.isBlacklisted(token)) {
                    log.error("Token is blacklisted");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token is blacklisted");
                    return;
                }

                if (!tokenService.existsByAccessToken(token)) {
                    log.error("Token does not exist in DB (maybe logout)");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token is invalid");
                    return;
                }

                username = jwtTokenProvider.getUsernameFromAccessToken(token);
            } else {
                log.error("Access Token is not valid");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
