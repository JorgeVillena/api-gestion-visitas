package com.demo.api_gestion_visitas.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VisitUpdateRequestDto(
        @NotBlank(message = "coordinatorId es obligatorio")
        String coordinatorId,
        @NotBlank(message = "promoterId es obligatorio")
        String promoterId,
        @NotBlank(message = "supervisorId es obligatorio")
        String supervisorId,
        @NotBlank(message = "placeName es obligatorio")
        String placeName,
        @NotNull(message = "scheduledDate es obligatorio")
        LocalDate scheduledDate,
        @NotBlank(message = "expectedStartTime es obligatorio")
        String expectedStartTime,
        @NotBlank(message = "expectedEndTime es obligatorio")
        String expectedEndTime,
        @NotNull(message = "latitude es obligatorio")
        Double latitude,
        @NotNull(message = "longitude es obligatorio")
        Double longitude,
        String status
) {
}
