package com.demo.api_gestion_visitas.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "visit_schedules")
public class VisitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long coordinatorId;

    @Column(nullable = false)
    private Long promoterId;

    @Column(nullable = false)
    private Long supervisorId;

    @Column(nullable = false, length = 255)
    private String coordinatorName;

    @Column(nullable = false, length = 255)
    private String promoterName;

    @Column(nullable = false, length = 500)
    private String placeName;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column(nullable = false, length = 16)
    private String expectedStartTime;

    @Column(nullable = false, length = 16)
    private String expectedEndTime;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false, length = 64)
    private String status;

    private Instant promoterArrivalConfirmedAt;
    private Instant promoterVisitClosedAt;
    private Instant coordinatorCheckInAt;
    private Instant coordinatorVisitClosedAt;

    @Column(nullable = false)
    private boolean notificationSentToSupervisor;

    private Double promoterArrivalLatitude;
    private Double promoterArrivalLongitude;
    private Double coordinatorCheckInLatitude;
    private Double coordinatorCheckInLongitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(Long coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public Long getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(Long promoterId) {
        this.promoterId = promoterId;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(Long supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public String getPromoterName() {
        return promoterName;
    }

    public void setPromoterName(String promoterName) {
        this.promoterName = promoterName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getExpectedStartTime() {
        return expectedStartTime;
    }

    public void setExpectedStartTime(String expectedStartTime) {
        this.expectedStartTime = expectedStartTime;
    }

    public String getExpectedEndTime() {
        return expectedEndTime;
    }

    public void setExpectedEndTime(String expectedEndTime) {
        this.expectedEndTime = expectedEndTime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getPromoterArrivalConfirmedAt() {
        return promoterArrivalConfirmedAt;
    }

    public void setPromoterArrivalConfirmedAt(Instant promoterArrivalConfirmedAt) {
        this.promoterArrivalConfirmedAt = promoterArrivalConfirmedAt;
    }

    public Instant getPromoterVisitClosedAt() {
        return promoterVisitClosedAt;
    }

    public void setPromoterVisitClosedAt(Instant promoterVisitClosedAt) {
        this.promoterVisitClosedAt = promoterVisitClosedAt;
    }

    public Instant getCoordinatorCheckInAt() {
        return coordinatorCheckInAt;
    }

    public void setCoordinatorCheckInAt(Instant coordinatorCheckInAt) {
        this.coordinatorCheckInAt = coordinatorCheckInAt;
    }

    public Instant getCoordinatorVisitClosedAt() {
        return coordinatorVisitClosedAt;
    }

    public void setCoordinatorVisitClosedAt(Instant coordinatorVisitClosedAt) {
        this.coordinatorVisitClosedAt = coordinatorVisitClosedAt;
    }

    public boolean isNotificationSentToSupervisor() {
        return notificationSentToSupervisor;
    }

    public void setNotificationSentToSupervisor(boolean notificationSentToSupervisor) {
        this.notificationSentToSupervisor = notificationSentToSupervisor;
    }

    public Double getPromoterArrivalLatitude() {
        return promoterArrivalLatitude;
    }

    public void setPromoterArrivalLatitude(Double promoterArrivalLatitude) {
        this.promoterArrivalLatitude = promoterArrivalLatitude;
    }

    public Double getPromoterArrivalLongitude() {
        return promoterArrivalLongitude;
    }

    public void setPromoterArrivalLongitude(Double promoterArrivalLongitude) {
        this.promoterArrivalLongitude = promoterArrivalLongitude;
    }

    public Double getCoordinatorCheckInLatitude() {
        return coordinatorCheckInLatitude;
    }

    public void setCoordinatorCheckInLatitude(Double coordinatorCheckInLatitude) {
        this.coordinatorCheckInLatitude = coordinatorCheckInLatitude;
    }

    public Double getCoordinatorCheckInLongitude() {
        return coordinatorCheckInLongitude;
    }

    public void setCoordinatorCheckInLongitude(Double coordinatorCheckInLongitude) {
        this.coordinatorCheckInLongitude = coordinatorCheckInLongitude;
    }
}
