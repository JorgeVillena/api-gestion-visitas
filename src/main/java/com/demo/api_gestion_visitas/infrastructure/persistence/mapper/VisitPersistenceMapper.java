package com.demo.api_gestion_visitas.infrastructure.persistence.mapper;

import com.demo.api_gestion_visitas.domain.model.Visit;
import com.demo.api_gestion_visitas.infrastructure.persistence.entity.VisitEntity;
import org.springframework.stereotype.Component;

@Component
public class VisitPersistenceMapper {
    public VisitEntity toEntity(Visit visit) {
        VisitEntity e = new VisitEntity();
        e.setId(visit.getId());
        e.setCoordinatorId(visit.getCoordinatorId());
        e.setPromoterId(visit.getPromoterId());
        e.setSupervisorId(visit.getSupervisorId());
        e.setCoordinatorName(visit.getCoordinatorName());
        e.setPromoterName(visit.getPromoterName());
        e.setPlaceName(visit.getPlaceName());
        e.setScheduledDate(visit.getScheduledDate());
        e.setExpectedStartTime(visit.getExpectedStartTime());
        e.setExpectedEndTime(visit.getExpectedEndTime());
        e.setLatitude(visit.getLatitude());
        e.setLongitude(visit.getLongitude());
        e.setStatus(visit.getStatus());
        e.setPromoterArrivalConfirmedAt(visit.getPromoterArrivalConfirmedAt());
        e.setPromoterVisitClosedAt(visit.getPromoterVisitClosedAt());
        e.setCoordinatorCheckInAt(visit.getCoordinatorCheckInAt());
        e.setCoordinatorVisitClosedAt(visit.getCoordinatorVisitClosedAt());
        e.setNotificationSentToSupervisor(visit.isNotificationSentToSupervisor());
        e.setPromoterArrivalLatitude(visit.getPromoterArrivalLatitude());
        e.setPromoterArrivalLongitude(visit.getPromoterArrivalLongitude());
        e.setCoordinatorCheckInLatitude(visit.getCoordinatorCheckInLatitude());
        e.setCoordinatorCheckInLongitude(visit.getCoordinatorCheckInLongitude());
        return e;
    }

    public Visit toDomain(VisitEntity e) {
        return new Visit(
                e.getId(),
                e.getCoordinatorId(),
                e.getPromoterId(),
                e.getSupervisorId(),
                e.getCoordinatorName(),
                e.getPromoterName(),
                e.getPlaceName(),
                e.getScheduledDate(),
                e.getExpectedStartTime(),
                e.getExpectedEndTime(),
                e.getLatitude(),
                e.getLongitude(),
                e.getStatus(),
                e.getPromoterArrivalConfirmedAt(),
                e.getPromoterVisitClosedAt(),
                e.getCoordinatorCheckInAt(),
                e.getCoordinatorVisitClosedAt(),
                e.isNotificationSentToSupervisor(),
                e.getPromoterArrivalLatitude(),
                e.getPromoterArrivalLongitude(),
                e.getCoordinatorCheckInLatitude(),
                e.getCoordinatorCheckInLongitude()
        );
    }
}
