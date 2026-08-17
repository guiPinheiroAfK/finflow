package com.finflow.application.usecase.auth;

import com.finflow.application.dto.auth.UserResponse;
import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetCurrentUserUseCase {

    private final UserRepository userRepository;

    public GetCurrentUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse execute(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", userId));
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
