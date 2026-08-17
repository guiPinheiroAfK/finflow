package com.finflow.application.dto.auth;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresInSeconds
) {
}
