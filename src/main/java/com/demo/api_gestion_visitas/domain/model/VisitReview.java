package com.demo.api_gestion_visitas.domain.model;

import java.time.Instant;

public class VisitReview {
    private final Long id;
    private final Long visitId;
    private final Long supervisorId;
    private final String finalStatus;
    private final String comment;
    private final Instant createdAt;

    public VisitReview(Long id, Long visitId, Long supervisorId, String finalStatus, String comment, Instant createdAt) {
        this.id = id;
        this.visitId = visitId;
        this.supervisorId = supervisorId;
        this.finalStatus = finalStatus;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getVisitId() {
        return visitId;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public String getFinalStatus() {
        return finalStatus;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
