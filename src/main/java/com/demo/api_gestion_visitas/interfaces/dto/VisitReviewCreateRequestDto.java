package com.demo.api_gestion_visitas.interfaces.dto;

import jakarta.validation.constraints.NotBlank;

public record VisitReviewCreateRequestDto(
        @NotBlank(message = "visitId es obligatorio")
        String visitId,
        @NotBlank(message = "finalStatus es obligatorio")
        String finalStatus,
        String comment
) {
}
