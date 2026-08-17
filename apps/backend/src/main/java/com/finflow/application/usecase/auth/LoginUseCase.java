package com.finflow.application.usecase.auth;

import com.finflow.application.dto.auth.LoginRequest;
import com.finflow.application.dto.auth.TokenResponse;
import com.finflow.application.exception.InvalidCredentialsException;
import com.finflow.domain.model.user.User;
import com.finflow.domain.repository.UserRepository;
import com.finflow.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public TokenResponse execute(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .filter(User::isActive)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        var access = jwtService.generateAccessToken(user.getId(), List.of(user.getRole().name()));
        var refresh = jwtService.generateRefreshToken(user.getId());

        long expiresIn = Duration.between(Instant.now(), access.expiresAt()).toSeconds();
        return new TokenResponse(access.token(), refresh.token(), expiresIn);
    }
}
