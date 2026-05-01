package com.demo.api_gestion_visitas.application.service;

import com.demo.api_gestion_visitas.application.port.SupervisorNotificationPort;
import com.demo.api_gestion_visitas.application.util.GeoUtils;
import com.demo.api_gestion_visitas.domain.model.Profile;
import com.demo.api_gestion_visitas.domain.model.User;
import com.demo.api_gestion_visitas.domain.model.Visit;
import com.demo.api_gestion_visitas.domain.model.VisitEvidence;
import com.demo.api_gestion_visitas.domain.repository.DeviceTokenRepository;
import com.demo.api_gestion_visitas.domain.repository.UserRepository;
import com.demo.api_gestion_visitas.domain.repository.VisitEvidenceRepository;
import com.demo.api_gestion_visitas.domain.repository.VisitRepository;
import com.demo.api_gestion_visitas.exception.BusinessException;
import com.demo.api_gestion_visitas.interfaces.dto.ActorPatchRequestDto;
import com.demo.api_gestion_visitas.interfaces.dto.VisitCreateRequestDto;
import com.demo.api_gestion_visitas.interfaces.dto.VisitUpdateRequestDto;
import com.demo.api_gestion_visitas.interfaces.dto.VisitValidationResponseDto;
import com.demo.api_gestion_visitas.infrastructure.storage.EvidenceStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
public class VisitService {
    public static final String STATUS_PROGRAMADA = "Programada";
    public static final String STATUS_EN_CURSO = "EnCurso";
    public static final String STATUS_PROMOTOR_CERRADO = "PromotorCerrado";
    public static final String STATUS_COMPLETA = "Completa";

    public static final String EVENT_PROMOTER_ARRIVAL = "PROMOTER_ARRIVAL";
    public static final String EVENT_PROMOTER_CLOSE = "PROMOTER_CLOSE";
    public static final String EVENT_COORDINATOR_CHECKIN = "COORDINATOR_CHECKIN";
    public static final String EVENT_COORDINATOR_CLOSE = "COORDINATOR_CLOSE";

    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final EvidenceStorageService evidenceStorageService;
    private final VisitEvidenceRepository visitEvidenceRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final SupervisorNotificationPort supervisorNotificationPort;
    private final double gpsThresholdMeters;

    public VisitService(
            VisitRepository visitRepository,
            UserRepository userRepository,
            EvidenceStorageService evidenceStorageService,
            VisitEvidenceRepository visitEvidenceRepository,
            DeviceTokenRepository deviceTokenRepository,
            SupervisorNotificationPort supervisorNotificationPort,
            @Value("${app.gps.validation.threshold-meters:200}") double gpsThresholdMeters
    ) {
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
        this.evidenceStorageService = evidenceStorageService;
        this.visitEvidenceRepository = visitEvidenceRepository;
        this.deviceTokenRepository = deviceTokenRepository;
        this.supervisorNotificationPort = supervisorNotificationPort;
        this.gpsThresholdMeters = gpsThresholdMeters;
    }

    public Visit create(VisitCreateRequestDto request) {
        long coordinatorId = parseUserId(request.coordinatorId(), "coordinatorId");
        long promoterId = parseUserId(request.promoterId(), "promoterId");
        long supervisorId = parseUserId(request.supervisorId(), "supervisorId");

        User coordinator = requireUserWithProfile(coordinatorId, Profile.COORDINADOR, "Coordinador invalido");
        User promoter = requireUserWithProfile(promoterId, Profile.PEC, "Promotor invalido");
        User supervisor = requireUserWithProfile(supervisorId, Profile.ESPECIALISTA, "Supervisor invalido");

        Visit visit = new Visit(
                null,
                coordinatorId,
                promoterId,
                supervisorId,
                coordinator.fullName().trim(),
                promoter.fullName().trim(),
                request.placeName(),
                request.scheduledDate(),
                request.expectedStartTime(),
                request.expectedEndTime(),
                request.latitude(),
                request.longitude(),
                STATUS_PROGRAMADA,
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                null,
                null
        );
        return visitRepository.save(visit);
    }

    public List<Visit> listForActor(User actor, String scopeParam) {
        String scope = normalizeScope(scopeParam, actor.getPerfil());
        return switch (scope) {
            case "promoter" -> visitRepository.findByPromoterIdOrdered(actor.getId());
            case "coordinator" -> visitRepository.findByCoordinatorIdOrdered(actor.getId());
            case "supervisor" -> visitRepository.findBySupervisorIdOrdered(actor.getId());
            case "director", "all" -> visitRepository.findAllOrdered();
            default -> throw new BusinessException("scope invalido", HttpStatus.BAD_REQUEST);
        };
    }

    public Visit findById(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Visita no encontrada", HttpStatus.NOT_FOUND));
    }

    public Visit update(Long id, VisitUpdateRequestDto request) {
        Visit existing = findById(id);
        long coordinatorId = parseUserId(request.coordinatorId(), "coordinatorId");
        long promoterId = parseUserId(request.promoterId(), "promoterId");
        long supervisorId = parseUserId(request.supervisorId(), "supervisorId");

        User coordinator = requireUserWithProfile(coordinatorId, Profile.COORDINADOR, "Coordinador invalido");
        User promoter = requireUserWithProfile(promoterId, Profile.PEC, "Promotor invalido");
        User supervisor = requireUserWithProfile(supervisorId, Profile.ESPECIALISTA, "Supervisor invalido");

        String status = request.status() != null && !request.status().isBlank() ? request.status() : existing.getStatus();

        Visit updated = new Visit(
                existing.getId(),
                coordinatorId,
                promoterId,
                supervisorId,
                coordinator.fullName().trim(),
                promoter.fullName().trim(),
                request.placeName(),
                request.scheduledDate(),
                request.expectedStartTime(),
                request.expectedEndTime(),
                request.latitude(),
                request.longitude(),
                status,
                existing.getPromoterArrivalConfirmedAt(),
                existing.getPromoterVisitClosedAt(),
                existing.getCoordinatorCheckInAt(),
                existing.getCoordinatorVisitClosedAt(),
                existing.isNotificationSentToSupervisor(),
                existing.getPromoterArrivalLatitude(),
                existing.getPromoterArrivalLongitude(),
                existing.getCoordinatorCheckInLatitude(),
                existing.getCoordinatorCheckInLongitude()
        );
        return visitRepository.save(updated);
    }

    public Visit patchPromoterArrival(Long visitId, ActorPatchRequestDto request, User actor) {
        Visit v = findById(visitId);
        assertActorPromoter(actor, v);
        Instant ts = Instant.now();
        saveOptionalEvidence(visitId, actor, EVENT_PROMOTER_ARRIVAL, request);

        Visit patched = Visit.builder(v)
                .promoterArrivalConfirmedAt(ts)
                .promoterArrivalLatitude(request.latitude())
                .promoterArrivalLongitude(request.longitude())
                .status(STATUS_EN_CURSO)
                .build();
        return visitRepository.save(patched);
    }

    public Visit patchPromoterClose(Long visitId, ActorPatchRequestDto request, User actor) {
        Visit v = findById(visitId);
        assertActorPromoter(actor, v);
        Instant ts = Instant.now();
        saveOptionalEvidence(visitId, actor, EVENT_PROMOTER_CLOSE, request);

        Visit patched = Visit.builder(v)
                .promoterVisitClosedAt(ts)
                .status(STATUS_PROMOTOR_CERRADO)
                .build();
        patched = visitRepository.save(patched);
        return finalizeNotificationIfNeeded(patched);
    }

    public Visit patchCoordinatorCheckIn(Long visitId, ActorPatchRequestDto request, User actor) {
        Visit v = findById(visitId);
        assertActorCoordinator(actor, v);
        Instant ts = Instant.now();
        saveOptionalEvidence(visitId, actor, EVENT_COORDINATOR_CHECKIN, request);

        Visit patched = Visit.builder(v)
                .coordinatorCheckInAt(ts)
                .coordinatorCheckInLatitude(request.latitude())
                .coordinatorCheckInLongitude(request.longitude())
                .build();
        return visitRepository.save(patched);
    }

    public Visit patchCoordinatorClose(Long visitId, ActorPatchRequestDto request, User actor) {
        Visit v = findById(visitId);
        assertActorCoordinator(actor, v);
        Instant ts = Instant.now();
        saveOptionalEvidence(visitId, actor, EVENT_COORDINATOR_CLOSE, request);

        Visit patched = Visit.builder(v)
                .coordinatorVisitClosedAt(ts)
                .status(STATUS_COMPLETA)
                .build();
        patched = visitRepository.save(patched);
        return finalizeNotificationIfNeeded(patched);
    }

    /**
     * Compatibilidad: equivale a {@link #patchPromoterArrival(Long, ActorPatchRequestDto, User)}.
     */
    public Visit registerLegacyEntry(Long visitId, com.demo.api_gestion_visitas.interfaces.dto.VisitEntryRequestDto request, User actor) {
        ActorPatchRequestDto mapped = new ActorPatchRequestDto(
                request.latitud(),
                request.longitud(),
                request.observacion(),
                request.evidenciaBase64()
        );
        return patchPromoterArrival(visitId, mapped, actor);
    }

    public VisitValidationResponseDto validation(Long visitId) {
        Visit v = findById(visitId);
        double d = GeoUtils.distanceMeters(
                v.getPromoterArrivalLatitude(),
                v.getPromoterArrivalLongitude(),
                v.getCoordinatorCheckInLatitude(),
                v.getCoordinatorCheckInLongitude()
        );
        if (Double.isNaN(d)) {
            return new VisitValidationResponseDto(
                    -1,
                    false,
                    "INCOMPLETE",
                    "Falta registro de ubicacion del promotor y/o del coordinador"
            );
        }
        boolean ok = d <= gpsThresholdMeters;
        return new VisitValidationResponseDto(
                Math.round(d * 100.0) / 100.0,
                ok,
                ok ? "OK" : "INCONSISTENT",
                ok ? "Distancia dentro del umbral configurado" : "Distancia mayor al umbral configurado"
        );
    }

    public List<Visit> findAllOrdered() {
        return visitRepository.findAllOrdered();
    }

    private Visit finalizeNotificationIfNeeded(Visit v) {
        if (v.getPromoterVisitClosedAt() == null || v.getCoordinatorVisitClosedAt() == null || v.isNotificationSentToSupervisor()) {
            return v;
        }
        String token = deviceTokenRepository.findByUserId(v.getSupervisorId())
                .map(com.demo.api_gestion_visitas.domain.model.DeviceToken::getFcmToken)
                .orElse(null);
        supervisorNotificationPort.notifyVisitReadyForReview(
                v.getSupervisorId(),
                v.getId(),
                v.getPlaceName(),
                v.getPromoterName(),
                token
        );
        Visit flagged = Visit.builder(v).notificationSentToSupervisor(true).build();
        return visitRepository.save(flagged);
    }

    private void saveOptionalEvidence(Long visitId, User actor, String eventType, ActorPatchRequestDto request) {
        String imagePath = evidenceStorageService.saveBase64Image(request.evidenciaBase64());
        boolean hasText = request.observation() != null && !request.observation().isBlank();
        if (imagePath == null && !hasText) {
            return;
        }
        VisitEvidence evidence = new VisitEvidence(
                null,
                visitId,
                actor.getId(),
                actor.getPerfil().name().toLowerCase(Locale.ROOT),
                eventType,
                imagePath,
                request.latitude(),
                request.longitude(),
                request.observation(),
                Instant.now()
        );
        visitEvidenceRepository.save(evidence);
    }

    private void assertActorPromoter(User actor, Visit v) {
        if (actor.getPerfil() != Profile.PEC || !actor.getId().equals(v.getPromoterId())) {
            throw new BusinessException("Solo el promotor asignado puede ejecutar esta accion", HttpStatus.FORBIDDEN);
        }
    }

    private void assertActorCoordinator(User actor, Visit v) {
        if (actor.getPerfil() != Profile.COORDINADOR || !actor.getId().equals(v.getCoordinatorId())) {
            throw new BusinessException("Solo el coordinador asignado puede ejecutar esta accion", HttpStatus.FORBIDDEN);
        }
    }

    private User requireUserWithProfile(long userId, Profile expected, String errorMessage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(errorMessage, HttpStatus.BAD_REQUEST));
        if (user.getPerfil() != expected) {
            throw new BusinessException(errorMessage, HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    private static long parseUserId(String raw, String field) {
        try {
            return Long.parseLong(raw.trim());
        } catch (Exception ex) {
            throw new BusinessException(field + " invalido", HttpStatus.BAD_REQUEST);
        }
    }

    private static String normalizeScope(String scopeParam, Profile perfil) {
        if (scopeParam != null && !scopeParam.isBlank()) {
            return scopeParam.trim().toLowerCase(Locale.ROOT);
        }
        return switch (perfil) {
            case PEC -> "promoter";
            case COORDINADOR -> "coordinator";
            case ESPECIALISTA -> "supervisor";
            case DIRECTOR -> "all";
        };
    }
}
