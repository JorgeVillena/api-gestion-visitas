package com.demo.api_gestion_visitas.domain.model;

import java.time.Instant;

public class VisitEvidence {
    private final Long id;
    private final Long visitId;
    private final Long userId;
    private final String userRole;
    private final String eventType;
    private final String imageUrl;
    private final Double latitude;
    private final Double longitude;
    private final String observation;
    private final Instant createdAt;

    public VisitEvidence(
            Long id,
            Long visitId,
            Long userId,
            String userRole,
            String eventType,
            String imageUrl,
            Double latitude,
            Double longitude,
            String observation,
            Instant createdAt
    ) {
        this.id = id;
        this.visitId = visitId;
        this.userId = userId;
        this.userRole = userRole;
        this.eventType = eventType;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.observation = observation;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getVisitId() {
        return visitId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getEventType() {
        return eventType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getObservation() {
        return observation;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
