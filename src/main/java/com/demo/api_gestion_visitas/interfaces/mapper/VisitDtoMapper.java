package com.demo.api_gestion_visitas.interfaces.mapper;

import com.demo.api_gestion_visitas.domain.model.Visit;
import com.demo.api_gestion_visitas.interfaces.dto.VisitResponseDto;
import org.springframework.stereotype.Component;

@Component
public class VisitDtoMapper {
    public VisitResponseDto toResponse(Visit visit) {
        return new VisitResponseDto(
                visit.getId() == null ? null : String.valueOf(visit.getId()),
                String.valueOf(visit.getCoordinatorId()),
                String.valueOf(visit.getPromoterId()),
                String.valueOf(visit.getSupervisorId()),
                visit.getCoordinatorName(),
                visit.getPromoterName(),
                visit.getPlaceName(),
                visit.getScheduledDate(),
                visit.getExpectedStartTime(),
                visit.getExpectedEndTime(),
                visit.getLatitude(),
                visit.getLongitude(),
                visit.getStatus(),
                visit.getPromoterArrivalConfirmedAt(),
                visit.getPromoterVisitClosedAt(),
                visit.getCoordinatorCheckInAt(),
                visit.getCoordinatorVisitClosedAt(),
                visit.isNotificationSentToSupervisor(),
                visit.getPromoterArrivalLatitude(),
                visit.getPromoterArrivalLongitude(),
                visit.getCoordinatorCheckInLatitude(),
                visit.getCoordinatorCheckInLongitude()
        );
    }
}
