package com.demo.api_gestion_visitas.interfaces.dto;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRegisterRequestDto(
        @NotBlank(message = "token es obligatorio")
        String token
) {
}
