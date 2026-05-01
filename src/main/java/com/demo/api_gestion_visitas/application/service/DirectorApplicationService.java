package com.demo.api_gestion_visitas.application.service;

import com.demo.api_gestion_visitas.application.util.GeoUtils;
import com.demo.api_gestion_visitas.domain.model.Visit;
import com.demo.api_gestion_visitas.domain.repository.VisitRepository;
import com.demo.api_gestion_visitas.domain.repository.VisitReviewRepository;
import com.demo.api_gestion_visitas.interfaces.dto.DirectorOverviewResponseDto;
import com.demo.api_gestion_visitas.interfaces.dto.DirectorReportResponseDto;
import com.demo.api_gestion_visitas.interfaces.dto.VisitResponseDto;
import com.demo.api_gestion_visitas.interfaces.mapper.VisitDtoMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
public class DirectorApplicationService {
    private final VisitRepository visitRepository;
    private final VisitReviewRepository visitReviewRepository;
    private final VisitDtoMapper visitDtoMapper;
    private final double gpsThresholdMeters;

    public DirectorApplicationService(
            VisitRepository visitRepository,
            VisitReviewRepository visitReviewRepository,
            VisitDtoMapper visitDtoMapper,
            @Value("${app.gps.validation.threshold-meters:200}") double gpsThresholdMeters
    ) {
        this.visitRepository = visitRepository;
        this.visitReviewRepository = visitReviewRepository;
        this.visitDtoMapper = visitDtoMapper;
        this.gpsThresholdMeters = gpsThresholdMeters;
    }

    public DirectorOverviewResponseDto overview() {
        List<Visit> all = visitRepository.findAllOrdered();
        long programadas = all.stream().filter(v -> VisitService.STATUS_PROGRAMADA.equals(v.getStatus())).count();
        long completas = all.stream().filter(v -> VisitService.STATUS_COMPLETA.equals(v.getStatus())).count();
        long total = all.size();
        double pct = total == 0 ? 0 : (completas * 100.0) / total;

        Map<Long, CoordinatorAgg> byCoord = new HashMap<>();
        for (Visit v : all) {
            CoordinatorAgg a = byCoord.computeIfAbsent(v.getCoordinatorId(), id -> new CoordinatorAgg(v.getCoordinatorName()));
            a.total++;
            if (VisitService.STATUS_COMPLETA.equals(v.getStatus())) {
                a.completas++;
            }
        }
        List<DirectorOverviewResponseDto.CoordinatorSummaryDto> destacados = byCoord.entrySet().stream()
                .map(e -> new DirectorOverviewResponseDto.CoordinatorSummaryDto(
                        String.valueOf(e.getKey()),
                        e.getValue().nombre,
                        e.getValue().completas,
                        e.getValue().total == 0 ? 0 : (e.getValue().completas * 100.0) / e.getValue().total
                ))
                .sorted(Comparator.comparingDouble(DirectorOverviewResponseDto.CoordinatorSummaryDto::cumplimientoPct).reversed())
                .limit(10)
                .toList();

        Map<String, Long> zonaInc = new HashMap<>();
        for (Visit v : all) {
            double d = GeoUtils.distanceMeters(
                    v.getPromoterArrivalLatitude(),
                    v.getPromoterArrivalLongitude(),
                    v.getCoordinatorCheckInLatitude(),
                    v.getCoordinatorCheckInLongitude()
            );
            if (!Double.isNaN(d) && d > gpsThresholdMeters) {
                String z = v.getPlaceName() != null ? v.getPlaceName() : "SIN_ZONA";
                zonaInc.merge(z, 1L, Long::sum);
            }
        }
        List<DirectorOverviewResponseDto.ZoneAlertDto> alertas = zonaInc.entrySet().stream()
                .map(e -> new DirectorOverviewResponseDto.ZoneAlertDto(e.getKey(), e.getValue()))
                .toList();

        WeekFields wf = WeekFields.of(Locale.getDefault());
        Map<String, TrendAcc> trend = new HashMap<>();
        for (Visit v : all) {
            LocalDate d = v.getScheduledDate();
            String key = d.getYear() + "-W" + d.get(wf.weekOfWeekBasedYear());
            TrendAcc t = trend.computeIfAbsent(key, k -> new TrendAcc());
            t.programadas++;
            if (VisitService.STATUS_COMPLETA.equals(v.getStatus())) {
                t.completas++;
            }
        }
        List<DirectorOverviewResponseDto.TrendPointDto> tendencia = trend.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new DirectorOverviewResponseDto.TrendPointDto(e.getKey(), e.getValue().completas, e.getValue().programadas))
                .toList();

        return new DirectorOverviewResponseDto(programadas, completas, Math.round(pct * 100.0) / 100.0, destacados, alertas, tendencia);
    }

    public DirectorReportResponseDto report(LocalDate from, LocalDate to, Long coordinatorIdFilter) {
        List<Visit> all = visitRepository.findAllOrdered().stream()
                .filter(v -> from == null || !v.getScheduledDate().isBefore(from))
                .filter(v -> to == null || !v.getScheduledDate().isAfter(to))
                .filter(v -> coordinatorIdFilter == null || Objects.equals(v.getCoordinatorId(), coordinatorIdFilter))
                .toList();

        List<VisitResponseDto> visitas = all.stream().map(visitDtoMapper::toResponse).toList();

        Map<Long, ReportCoord> coords = new HashMap<>();
        for (Visit v : all) {
            ReportCoord rc = coords.computeIfAbsent(v.getCoordinatorId(), id -> new ReportCoord(v.getCoordinatorName()));
            rc.total++;
            if (VisitService.STATUS_COMPLETA.equals(v.getStatus())) {
                rc.completas++;
            }
        }
        List<DirectorReportResponseDto.CumplimientoDto> cumplimiento = coords.entrySet().stream()
                .map(e -> new DirectorReportResponseDto.CumplimientoDto(
                        String.valueOf(e.getKey()),
                        e.getValue().nombre,
                        e.getValue().total,
                        e.getValue().completas,
                        e.getValue().total == 0 ? 0 : Math.round((e.getValue().completas * 10000.0) / e.getValue().total) / 100.0
                ))
                .toList();

        List<DirectorReportResponseDto.InconsistenciaDto> inconsistencias = new ArrayList<>();
        for (Visit v : all) {
            double d = GeoUtils.distanceMeters(
                    v.getPromoterArrivalLatitude(),
                    v.getPromoterArrivalLongitude(),
                    v.getCoordinatorCheckInLatitude(),
                    v.getCoordinatorCheckInLongitude()
            );
            if (!Double.isNaN(d) && d > gpsThresholdMeters) {
                inconsistencias.add(new DirectorReportResponseDto.InconsistenciaDto(
                        String.valueOf(v.getId()),
                        "GPS",
                        "Distancia promotor-coordinador " + Math.round(d) + " m"
                ));
            }
            if (VisitService.STATUS_COMPLETA.equals(v.getStatus())
                    && visitReviewRepository.findByVisitId(v.getId()).isEmpty()) {
                inconsistencias.add(new DirectorReportResponseDto.InconsistenciaDto(
                        String.valueOf(v.getId()),
                        "REVISION",
                        "Visita completa sin revision del supervisor"
                ));
            }
        }

        return new DirectorReportResponseDto(visitas, cumplimiento, inconsistencias);
    }

    private static final class CoordinatorAgg {
        final String nombre;
        long total;
        long completas;

        CoordinatorAgg(String nombre) {
            this.nombre = nombre;
        }
    }

    private static final class TrendAcc {
        long programadas;
        long completas;
    }

    private static final class ReportCoord {
        final String nombre;
        long total;
        long completas;

        ReportCoord(String nombre) {
            this.nombre = nombre;
        }
    }
}
