package com.demo.api_gestion_visitas.domain.repository;

import com.demo.api_gestion_visitas.domain.model.DeviceToken;

import java.util.Optional;

public interface DeviceTokenRepository {
    DeviceToken save(DeviceToken token);

    Optional<DeviceToken> findByUserId(Long userId);
}
