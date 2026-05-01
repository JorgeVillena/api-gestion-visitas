package com.demo.api_gestion_visitas.interfaces.dto;

import com.demo.api_gestion_visitas.domain.model.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserUpdateRequestDto(
        @NotBlank(message = "nombres es obligatorio")
        String nombres,
        @NotBlank(message = "apellidos es obligatorio")
        String apellidos,
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
