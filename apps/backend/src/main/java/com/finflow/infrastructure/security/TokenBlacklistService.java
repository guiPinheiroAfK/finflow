package com.finflow.infrastructure.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * ADR-0005 §2: revogação por jti no Redis. TTL da entrada = tempo restante
 * até a expiração natural do token -- a blacklist se autolimpa, nunca cresce
 * além do necessário, e nunca precisa de job de faxina.
 */
@Service
public class TokenBlacklistService {

    private static final String KEY_PREFIX = "blacklist:jti:";

    private final StringRedisTemplate redis;

    public TokenBlacklistService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void revoke(String jti, Instant tokenExpiry) {
        long ttlSeconds = Duration.between(Instant.now(), tokenExpiry).toSeconds();
        if (ttlSeconds > 0) {
            redis.opsForValue().set(KEY_PREFIX + jti, "1", Duration.ofSeconds(ttlSeconds));
        }
        // ttlSeconds <= 0: token já expirado naturalmente, revogar é no-op.
    }

    public boolean isRevoked(String jti) {
        return Boolean.TRUE.equals(redis.hasKey(KEY_PREFIX + jti));
    }
}
