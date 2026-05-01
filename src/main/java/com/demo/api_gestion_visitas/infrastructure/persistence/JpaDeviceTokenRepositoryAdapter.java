package com.demo.api_gestion_visitas.infrastructure.persistence;

import com.demo.api_gestion_visitas.domain.model.DeviceToken;
import com.demo.api_gestion_visitas.domain.repository.DeviceTokenRepository;
import com.demo.api_gestion_visitas.infrastructure.persistence.entity.DeviceTokenEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaDeviceTokenRepositoryAdapter implements DeviceTokenRepository {
    private final SpringDataDeviceTokenRepository repo;

    public JpaDeviceTokenRepositoryAdapter(SpringDataDeviceTokenRepository repo) {
        this.repo = repo;
    }

    @Override
    public DeviceToken save(DeviceToken t) {
        DeviceTokenEntity e = new DeviceTokenEntity();
        e.setId(t.getId());
        e.setUserId(t.getUserId());
        e.setFcmToken(t.getFcmToken());
        e.setUpdatedAt(t.getUpdatedAt());
        DeviceTokenEntity saved = repo.save(e);
        return new DeviceToken(saved.getId(), saved.getUserId(), saved.getFcmToken(), saved.getUpdatedAt());
    }

    @Override
    public Optional<DeviceToken> findByUserId(Long userId) {
        return repo.findByUserId(userId).map(x -> new DeviceToken(x.getId(), x.getUserId(), x.getFcmToken(), x.getUpdatedAt()));
    }
}
