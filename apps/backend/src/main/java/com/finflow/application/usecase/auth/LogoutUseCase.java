package com.finflow.application.usecase.auth;

import com.finflow.infrastructure.security.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import com.finflow.infrastructure.security.JwtService;
import org.springframework.stereotype.Service;

/** ADR-0005 §2: logout revoga tanto o access quanto o refresh token -- só um dos dois não fecha a sessão. */
@Service
public class LogoutUseCase {

    private final JwtService jwtService;
    private final TokenBlacklistService blacklist;

    public LogoutUseCase(JwtService jwtService, TokenBlacklistService blacklist) {
        this.jwtService = jwtService;
        this.blacklist = blacklist;
    }

    public void execute(String accessToken, String refreshToken) {
        revokeIfValid(accessToken);
        revokeIfValid(refreshToken);
    }

    private void revokeIfValid(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        try {
            Claims claims = jwtService.parseAndValidate(token);
            blacklist.revoke(claims.getId(), claims.getExpiration().toInstant());
        } catch (JwtException e) {
            // já inválido/expirado -- nada a revogar
        }
    }
}
