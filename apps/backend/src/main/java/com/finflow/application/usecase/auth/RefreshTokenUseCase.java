package com.finflow.application.usecase.auth;

import com.finflow.application.dto.auth.TokenResponse;
import com.finflow.application.exception.InvalidTokenException;
import com.finflow.domain.repository.UserRepository;
import com.finflow.infrastructure.security.JwtService;
import com.finflow.infrastructure.security.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenUseCase {

    private final JwtService jwtService;
    private final TokenBlacklistService blacklist;
    private final UserRepository userRepository;

    public RefreshTokenUseCase(JwtService jwtService, TokenBlacklistService blacklist, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.blacklist = blacklist;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public TokenResponse execute(String refreshToken) {
        Claims claims;
        try {
            claims = jwtService.parseAndValidate(refreshToken);
        } catch (JwtException e) {
            throw new InvalidTokenException("Refresh token inválido ou expirado");
        }

        // ADR-0005 §2: refresh só é aceito se ainda não foi revogado (logout já invalidou seu jti)
        if (blacklist.isRevoked(claims.getId())) {
            throw new InvalidTokenException("Refresh token revogado");
        }

        UUID userId = UUID.fromString(claims.getSubject());
        var user = userRepository.findById(userId).orElseThrow(() -> new InvalidTokenException("Usuário inválido"));

        // Rotação: o refresh usado morre aqui, mesmo dentro da validade -- limita o
        // uso a uma vez e reduz a janela de replay caso o token vaze.
        blacklist.revoke(claims.getId(), claims.getExpiration().toInstant());

        var access = jwtService.generateAccessToken(user.getId(), List.of(user.getRole().name()));
        var newRefresh = jwtService.generateRefreshToken(user.getId());

        long expiresIn = Duration.between(Instant.now(), access.expiresAt()).toSeconds();
        return new TokenResponse(access.token(), newRefresh.token(), expiresIn);
    }
}
