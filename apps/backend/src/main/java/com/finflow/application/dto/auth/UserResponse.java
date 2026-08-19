package com.finflow.application.dto.auth;

import com.finflow.domain.model.user.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Role role
) {
}
