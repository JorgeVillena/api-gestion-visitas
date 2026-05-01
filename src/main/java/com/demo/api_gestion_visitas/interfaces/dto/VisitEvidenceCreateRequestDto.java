package com.demo.api_gestion_visitas.interfaces.dto;

import com.demo.api_gestion_visitas.domain.model.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VisitEvidenceCreateRequestDto(
        @NotBlank(message = "visitId es obligatorio")
        String visitId,
        @NotBlank(message = "imageBase64 es obligatorio")
        String imageBase64,
        Double latitude,
        Double longitude,
        String observation,
        @NotNull(message = "userRole es obligatorio")
        Profile userRole,
        @NotBlank(message = "eventType es obligatorio")
        String eventType
) {
}
