package com.finflow.application.dto.auth;

import com.finflow.domain.model.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "Senha deve ter ao menos 8 caracteres") String password,
        @NotNull Role role
) {
}
