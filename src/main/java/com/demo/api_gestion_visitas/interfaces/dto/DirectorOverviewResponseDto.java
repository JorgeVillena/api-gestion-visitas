package com.demo.api_gestion_visitas.interfaces.dto;

import java.util.List;

public record DirectorOverviewResponseDto(
        long totalVisitasProgramadas,
        long totalVisitasCompletas,
        double porcentajeCumplimiento,
        List<CoordinatorSummaryDto> coordinadoresDestacados,
        List<ZoneAlertDto> alertasPorZona,
        List<TrendPointDto> tendenciaSemanal
) {
    public record CoordinatorSummaryDto(String coordinatorId, String nombre, long visitasCompletas, double cumplimientoPct) {
    }

    public record ZoneAlertDto(String zona, long inconsistencias) {
    }

    public record TrendPointDto(String semana, long completas, long programadas) {
    }
}
