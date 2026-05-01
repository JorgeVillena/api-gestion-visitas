package com.demo.api_gestion_visitas.interfaces.dto;

import java.time.Instant;

public record VisitReviewResponseDto(
        String id,
        String visitId,
        String supervisorId,
        String finalStatus,
        String comment,
        Instant createdAt
) {
}
