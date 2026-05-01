package com.demo.api_gestion_visitas.interfaces.dto;

import java.util.List;

public record DirectorReportResponseDto(
        List<VisitResponseDto> visitas,
        List<CumplimientoDto> cumplimientoPorCoordinador,
        List<InconsistenciaDto> inconsistencias
) {
    public record CumplimientoDto(String coordinatorId, String nombre, long total, long completas, double pct) {
    }

    public record InconsistenciaDto(String visitId, String motivo, String detalle) {
    }
}
