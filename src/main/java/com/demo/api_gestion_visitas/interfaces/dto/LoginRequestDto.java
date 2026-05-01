package com.demo.api_gestion_visitas.interfaces.dto;

import com.demo.api_gestion_visitas.domain.model.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(
        @NotBlank(message = "usuario es obligatorio")
        String usuario,
        @NotBlank(message = "password es obligatorio")
        String password,
        @NotNull(message = "perfil es obligatorio")
        Profile perfil
) {
}
