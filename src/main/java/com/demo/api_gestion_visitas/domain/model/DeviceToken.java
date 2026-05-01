package com.demo.api_gestion_visitas.domain.model;

import java.time.Instant;

public class DeviceToken {
    private final Long id;
    private final Long userId;
    private final String fcmToken;
    private final Instant updatedAt;

    public DeviceToken(Long id, Long userId, String fcmToken, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
