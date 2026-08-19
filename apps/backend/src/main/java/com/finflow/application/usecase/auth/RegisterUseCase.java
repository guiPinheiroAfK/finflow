package com.finflow.application.usecase.auth;

import com.finflow.application.dto.auth.RegisterRequest;
import com.finflow.application.exception.EmailAlreadyRegisteredException;
import com.finflow.domain.model.user.User;
import com.finflow.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User execute(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyRegisteredException(request.email());
        }
        User user = User.register(
                request.name(), request.email(), passwordEncoder.encode(request.password()), request.role());
        return userRepository.save(user);
    }
}
