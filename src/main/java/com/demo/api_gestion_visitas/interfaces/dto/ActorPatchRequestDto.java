package com.demo.api_gestion_visitas.interfaces.dto;

import jakarta.validation.constraints.NotNull;

public record ActorPatchRequestDto(
        @NotNull(message = "latitude es obligatorio")
        Double latitude,
        @NotNull(message = "longitude es obligatorio")
        Double longitude,
        String observation,
        String evidenciaBase64
) {
}
