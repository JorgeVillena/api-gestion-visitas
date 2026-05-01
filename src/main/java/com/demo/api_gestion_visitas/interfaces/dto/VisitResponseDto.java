package com.demo.api_gestion_visitas.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VisitResponseDto(
        String id,
        String coordinatorId,
        String promoterId,
        String supervisorId,
        String coordinatorName,
        String promoterName,
        String placeName,
        LocalDate scheduledDate,
        String expectedStartTime,
        String expectedEndTime,
        Double latitude,
        Double longitude,
        String status,
        Instant promoterArrivalConfirmedAt,
        Instant promoterVisitClosedAt,
        Instant coordinatorCheckInAt,
        Instant coordinatorVisitClosedAt,
        boolean notificationSentToSupervisor,
        Double promoterArrivalLatitude,
        Double promoterArrivalLongitude,
        Double coordinatorCheckInLatitude,
        Double coordinatorCheckInLongitude
) {
}
