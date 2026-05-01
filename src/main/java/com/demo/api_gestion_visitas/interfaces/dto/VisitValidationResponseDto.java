package com.demo.api_gestion_visitas.interfaces.dto;

public record VisitValidationResponseDto(
        double distanceInMeters,
        boolean isConsistent,
        String status,
        String message
) {
}
