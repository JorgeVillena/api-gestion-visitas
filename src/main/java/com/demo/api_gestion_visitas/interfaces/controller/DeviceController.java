package com.demo.api_gestion_visitas.interfaces.controller;

import com.demo.api_gestion_visitas.application.service.AuthService;
import com.demo.api_gestion_visitas.application.service.DeviceTokenApplicationService;
import com.demo.api_gestion_visitas.interfaces.dto.FcmTokenRegisterRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/devices")
public class DeviceController {
    private final DeviceTokenApplicationService deviceTokenApplicationService;
    private final AuthService authService;

    public DeviceController(DeviceTokenApplicationService deviceTokenApplicationService, AuthService authService) {
        this.deviceTokenApplicationService = deviceTokenApplicationService;
        this.authService = authService;
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<Void> registerFcmToken(Authentication authentication, @Valid @RequestBody FcmTokenRegisterRequestDto request) {
        deviceTokenApplicationService.register(authService.getByUsername(authentication.getName()), request.token());
        return ResponseEntity.noContent().build();
    }
}
