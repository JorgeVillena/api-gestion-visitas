package com.demo.api_gestion_visitas.domain.model;

import java.time.Instant;
import java.time.LocalDate;

public class Visit {
    private final Long id;
    private final Long coordinatorId;
    private final Long promoterId;
    private final Long supervisorId;
    private final String coordinatorName;
    private final String promoterName;
    private final String placeName;
    private final LocalDate scheduledDate;
    private final String expectedStartTime;
    private final String expectedEndTime;
    private final Double latitude;
    private final Double longitude;
    private final String status;
    private final Instant promoterArrivalConfirmedAt;
    private final Instant promoterVisitClosedAt;
    private final Instant coordinatorCheckInAt;
    private final Instant coordinatorVisitClosedAt;
    private final boolean notificationSentToSupervisor;
    private final Double promoterArrivalLatitude;
    private final Double promoterArrivalLongitude;
    private final Double coordinatorCheckInLatitude;
    private final Double coordinatorCheckInLongitude;

    public Visit(
            Long id,
            Long coordinatorId,
            Long promoterId,
            Long supervisorId,
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
        this.id = id;
        this.coordinatorId = coordinatorId;
        this.promoterId = promoterId;
        this.supervisorId = supervisorId;
        this.coordinatorName = coordinatorName;
        this.promoterName = promoterName;
        this.placeName = placeName;
        this.scheduledDate = scheduledDate;
        this.expectedStartTime = expectedStartTime;
        this.expectedEndTime = expectedEndTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.promoterArrivalConfirmedAt = promoterArrivalConfirmedAt;
        this.promoterVisitClosedAt = promoterVisitClosedAt;
        this.coordinatorCheckInAt = coordinatorCheckInAt;
        this.coordinatorVisitClosedAt = coordinatorVisitClosedAt;
        this.notificationSentToSupervisor = notificationSentToSupervisor;
        this.promoterArrivalLatitude = promoterArrivalLatitude;
        this.promoterArrivalLongitude = promoterArrivalLongitude;
        this.coordinatorCheckInLatitude = coordinatorCheckInLatitude;
        this.coordinatorCheckInLongitude = coordinatorCheckInLongitude;
    }

    public Long getId() {
        return id;
    }

    public Long getCoordinatorId() {
        return coordinatorId;
    }

    public Long getPromoterId() {
        return promoterId;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public String getPromoterName() {
        return promoterName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public String getExpectedStartTime() {
        return expectedStartTime;
    }

    public String getExpectedEndTime() {
        return expectedEndTime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getStatus() {
        return status;
    }

    public Instant getPromoterArrivalConfirmedAt() {
        return promoterArrivalConfirmedAt;
    }

    public Instant getPromoterVisitClosedAt() {
        return promoterVisitClosedAt;
    }

    public Instant getCoordinatorCheckInAt() {
        return coordinatorCheckInAt;
    }

    public Instant getCoordinatorVisitClosedAt() {
        return coordinatorVisitClosedAt;
    }

    public boolean isNotificationSentToSupervisor() {
        return notificationSentToSupervisor;
    }

    public Double getPromoterArrivalLatitude() {
        return promoterArrivalLatitude;
    }

    public Double getPromoterArrivalLongitude() {
        return promoterArrivalLongitude;
    }

    public Double getCoordinatorCheckInLatitude() {
        return coordinatorCheckInLatitude;
    }

    public Double getCoordinatorCheckInLongitude() {
        return coordinatorCheckInLongitude;
    }

    public static Builder builder(Visit source) {
        return new Builder(source);
    }

    public static final class Builder {
        private Long id;
        private Long coordinatorId;
        private Long promoterId;
        private Long supervisorId;
        private String coordinatorName;
        private String promoterName;
        private String placeName;
        private LocalDate scheduledDate;
        private String expectedStartTime;
        private String expectedEndTime;
        private Double latitude;
        private Double longitude;
        private String status;
        private Instant promoterArrivalConfirmedAt;
        private Instant promoterVisitClosedAt;
        private Instant coordinatorCheckInAt;
        private Instant coordinatorVisitClosedAt;
        private boolean notificationSentToSupervisor;
        private Double promoterArrivalLatitude;
        private Double promoterArrivalLongitude;
        private Double coordinatorCheckInLatitude;
        private Double coordinatorCheckInLongitude;

        private Builder(Visit source) {
            this.id = source.id;
            this.coordinatorId = source.coordinatorId;
            this.promoterId = source.promoterId;
            this.supervisorId = source.supervisorId;
            this.coordinatorName = source.coordinatorName;
            this.promoterName = source.promoterName;
            this.placeName = source.placeName;
            this.scheduledDate = source.scheduledDate;
            this.expectedStartTime = source.expectedStartTime;
            this.expectedEndTime = source.expectedEndTime;
            this.latitude = source.latitude;
            this.longitude = source.longitude;
            this.status = source.status;
            this.promoterArrivalConfirmedAt = source.promoterArrivalConfirmedAt;
            this.promoterVisitClosedAt = source.promoterVisitClosedAt;
            this.coordinatorCheckInAt = source.coordinatorCheckInAt;
            this.coordinatorVisitClosedAt = source.coordinatorVisitClosedAt;
            this.notificationSentToSupervisor = source.notificationSentToSupervisor;
            this.promoterArrivalLatitude = source.promoterArrivalLatitude;
            this.promoterArrivalLongitude = source.promoterArrivalLongitude;
            this.coordinatorCheckInLatitude = source.coordinatorCheckInLatitude;
            this.coordinatorCheckInLongitude = source.coordinatorCheckInLongitude;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder coordinatorId(Long coordinatorId) {
            this.coordinatorId = coordinatorId;
            return this;
        }

        public Builder promoterId(Long promoterId) {
            this.promoterId = promoterId;
            return this;
        }

        public Builder supervisorId(Long supervisorId) {
            this.supervisorId = supervisorId;
            return this;
        }

        public Builder coordinatorName(String coordinatorName) {
            this.coordinatorName = coordinatorName;
            return this;
        }

        public Builder promoterName(String promoterName) {
            this.promoterName = promoterName;
            return this;
        }

        public Builder placeName(String placeName) {
            this.placeName = placeName;
            return this;
        }

        public Builder scheduledDate(LocalDate scheduledDate) {
            this.scheduledDate = scheduledDate;
            return this;
        }

        public Builder expectedStartTime(String expectedStartTime) {
            this.expectedStartTime = expectedStartTime;
            return this;
        }

        public Builder expectedEndTime(String expectedEndTime) {
            this.expectedEndTime = expectedEndTime;
            return this;
        }

        public Builder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder promoterArrivalConfirmedAt(Instant promoterArrivalConfirmedAt) {
            this.promoterArrivalConfirmedAt = promoterArrivalConfirmedAt;
            return this;
        }

        public Builder promoterVisitClosedAt(Instant promoterVisitClosedAt) {
            this.promoterVisitClosedAt = promoterVisitClosedAt;
            return this;
        }

        public Builder coordinatorCheckInAt(Instant coordinatorCheckInAt) {
            this.coordinatorCheckInAt = coordinatorCheckInAt;
            return this;
        }

        public Builder coordinatorVisitClosedAt(Instant coordinatorVisitClosedAt) {
            this.coordinatorVisitClosedAt = coordinatorVisitClosedAt;
            return this;
        }

        public Builder notificationSentToSupervisor(boolean notificationSentToSupervisor) {
            this.notificationSentToSupervisor = notificationSentToSupervisor;
            return this;
        }

        public Builder promoterArrivalLatitude(Double promoterArrivalLatitude) {
            this.promoterArrivalLatitude = promoterArrivalLatitude;
            return this;
        }

        public Builder promoterArrivalLongitude(Double promoterArrivalLongitude) {
            this.promoterArrivalLongitude = promoterArrivalLongitude;
            return this;
        }

        public Builder coordinatorCheckInLatitude(Double coordinatorCheckInLatitude) {
            this.coordinatorCheckInLatitude = coordinatorCheckInLatitude;
            return this;
        }

        public Builder coordinatorCheckInLongitude(Double coordinatorCheckInLongitude) {
            this.coordinatorCheckInLongitude = coordinatorCheckInLongitude;
            return this;
        }

        public Visit build() {
            return new Visit(
                    id,
                    coordinatorId,
                    promoterId,
                    supervisorId,
                    coordinatorName,
                    promoterName,
                    placeName,
                    scheduledDate,
                    expectedStartTime,
                    expectedEndTime,
                    latitude,
                    longitude,
                    status,
                    promoterArrivalConfirmedAt,
                    promoterVisitClosedAt,
                    coordinatorCheckInAt,
                    coordinatorVisitClosedAt,
                    notificationSentToSupervisor,
                    promoterArrivalLatitude,
                    promoterArrivalLongitude,
                    coordinatorCheckInLatitude,
                    coordinatorCheckInLongitude
            );
        }
    }
}
