package com.demo.api_gestion_visitas.interfaces.dto;

import com.demo.api_gestion_visitas.domain.model.Profile;

import java.time.LocalDate;

public record AuthResponseDto(
        String token,
        Long id,
        String username,
        String fullName,
        Profile role,
        String nombres,
        String apellidos,
        Profile perfil,
        String schoolName,
        String modularCode,
        String ugelName,
        String locationName,
        String documentNumber,
        LocalDate birthDate
) {
}
