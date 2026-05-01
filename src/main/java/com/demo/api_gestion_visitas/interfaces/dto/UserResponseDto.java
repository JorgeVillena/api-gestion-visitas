package com.demo.api_gestion_visitas.interfaces.dto;

import com.demo.api_gestion_visitas.domain.model.Profile;

import java.time.LocalDate;

public record UserResponseDto(
        Long id,
        String nombres,
        String apellidos,
        String usuario,
        Profile perfil,
        String fullName,
        String schoolName,
        String modularCode,
        String ugelName,
        String locationName,
        String documentNumber,
        LocalDate birthDate
) {
}
