package com.demo.api_gestion_visitas.interfaces.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Legacy body for {@code POST /visitas/{id}/registrar-entrada} (mapea a llegada del promotor).
 */
public record VisitEntryRequestDto(
        LocalDateTime fechaHoraEntrada,
        @NotNull(message = "latitud es obligatorio")
        Double latitud,
        @NotNull(message = "longitud es obligatorio")
        Double longitud,
        String observacion,
        String evidenciaBase64
) {
}
