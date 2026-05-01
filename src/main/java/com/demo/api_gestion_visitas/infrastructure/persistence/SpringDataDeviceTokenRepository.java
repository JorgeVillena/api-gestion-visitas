package com.demo.api_gestion_visitas.infrastructure.persistence;

import com.demo.api_gestion_visitas.infrastructure.persistence.entity.DeviceTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataDeviceTokenRepository extends JpaRepository<DeviceTokenEntity, Long> {
    Optional<DeviceTokenEntity> findByUserId(Long userId);
}
