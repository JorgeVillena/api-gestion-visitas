package com.demo.api_gestion_visitas.application.service;

import com.demo.api_gestion_visitas.domain.model.DeviceToken;
import com.demo.api_gestion_visitas.domain.model.User;
import com.demo.api_gestion_visitas.domain.repository.DeviceTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class DeviceTokenApplicationService {
    private final DeviceTokenRepository deviceTokenRepository;

    public DeviceTokenApplicationService(DeviceTokenRepository deviceTokenRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
    }

    public DeviceToken register(User user, String token) {
        Instant now = Instant.now();
        return deviceTokenRepository.findByUserId(user.getId())
                .map(existing -> deviceTokenRepository.save(new DeviceToken(existing.getId(), user.getId(), token, now)))
                .orElseGet(() -> deviceTokenRepository.save(new DeviceToken(null, user.getId(), token, now)));
    }
}
