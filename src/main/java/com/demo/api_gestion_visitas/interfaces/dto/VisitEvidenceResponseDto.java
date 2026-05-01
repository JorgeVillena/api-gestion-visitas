package com.demo.api_gestion_visitas.interfaces.dto;

import java.time.Instant;

public record VisitEvidenceResponseDto(
        String id,
        String visitId,
        String userId,
        String userRole,
        String eventType,
        String imageUrl,
        Double latitude,
        Double longitude,
        String observation,
        Instant createdAt
) {
}
