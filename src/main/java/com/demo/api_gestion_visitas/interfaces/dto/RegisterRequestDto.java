package com.demo.api_gestion_visitas.interfaces.dto;

import com.demo.api_gestion_visitas.domain.model.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequestDto(
        @NotBlank(message = "nombres es obligatorio")
        String nombres,
        @NotBlank(message = "apellidos es obligatorio")
        String apellidos,
        @NotBlank(message = "usuario es obligatorio")
        String usuario,
        @NotBlank(message = "password es obligatorio")
        @Size(min = 6, message = "password debe tener al menos 6 caracteres")
        String password,
        @NotNull(message = "perfil es obligatorio")
        Profile perfil,
        String schoolName,
        String modularCode,
        String ugelName,
        String locationName,
        String documentNumber,
        LocalDate birthDate
) {
}
