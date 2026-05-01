package com.demo.api_gestion_visitas.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ActorPatchResponseDto(
        String visitId,
        Instant timestamp,
        String message,
        Boolean notificationSentToSupervisor
) {
}
