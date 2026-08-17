package com.finflow.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * ADR-0005: access token de vida curta (claims: sub, jti, roles) + refresh
 * token de vida longa, ambos com jti próprio para permitir revogação
 * individual via {@link TokenBlacklistService}.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;

    public JwtService(
            @Value("${finflow.jwt.secret}") String secret,
            @Value("${finflow.jwt.access-token-ttl-minutes}") long accessTokenTtlMinutes,
            @Value("${finflow.jwt.refresh-token-ttl-days}") long refreshTokenTtlDays) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTtl = Duration.ofMinutes(accessTokenTtlMinutes);
        this.refreshTokenTtl = Duration.ofDays(refreshTokenTtlDays);
    }

    public IssuedToken generateAccessToken(UUID userId, List<String> roles) {
        String jti = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(accessTokenTtl);

        String token = Jwts.builder()
                .subject(userId.toString())
                .id(jti)
                .claim("roles", roles)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();

        return new IssuedToken(token, jti, expiry);
    }

    public IssuedToken generateRefreshToken(UUID userId) {
        String jti = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(refreshTokenTtl);

        String token = Jwts.builder()
                .subject(userId.toString())
                .id(jti)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();

        return new IssuedToken(token, jti, expiry);
    }

    /** @throws JwtException se a assinatura ou expiração forem inválidas. */
    public Claims parseAndValidate(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public record IssuedToken(String token, String jti, Instant expiresAt) {
    }
}
