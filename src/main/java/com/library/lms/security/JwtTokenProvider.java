package com.library.lms.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenProvider {

    private final String secretKey;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;

    public JwtTokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-expiry}") long accessTokenExpiry,
        @Value("${jwt.refresh-token-expiry}") long refreshTokenExpiry
    ) {
        this.secretKey = secret;
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public String generateAccessToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return buildToken(principal.getUsername(), accessTokenExpiry, "ACCESS");
    }

    public String generateRefreshToken(String email) {
        return buildToken(email, refreshTokenExpiry, "REFRESH");
    }

    private String buildToken(String subject, long expiry, String type) {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("type", type);
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiry))
            .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
            .compact();
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Invalid JWT: {}", ex.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .setSigningKey(secretKey.getBytes())
            .parseClaimsJws(token)
            .getBody();
    }
}
