package com.demo.api_gestion_visitas.application.service;

import com.demo.api_gestion_visitas.domain.model.User;
import com.demo.api_gestion_visitas.domain.model.VisitEvidence;
import com.demo.api_gestion_visitas.domain.repository.VisitEvidenceRepository;
import com.demo.api_gestion_visitas.domain.repository.VisitRepository;
import com.demo.api_gestion_visitas.exception.BusinessException;
import com.demo.api_gestion_visitas.interfaces.dto.VisitEvidenceCreateRequestDto;
import com.demo.api_gestion_visitas.infrastructure.storage.EvidenceStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
public class VisitEvidenceApplicationService {
    private final VisitRepository visitRepository;
    private final VisitEvidenceRepository visitEvidenceRepository;
    private final EvidenceStorageService evidenceStorageService;

    public VisitEvidenceApplicationService(
            VisitRepository visitRepository,
            VisitEvidenceRepository visitEvidenceRepository,
            EvidenceStorageService evidenceStorageService
    ) {
        this.visitRepository = visitRepository;
        this.visitEvidenceRepository = visitEvidenceRepository;
        this.evidenceStorageService = evidenceStorageService;
    }

    public VisitEvidence create(User actor, VisitEvidenceCreateRequestDto dto) {
        long visitId = parseLong(dto.visitId(), "visitId");
        visitRepository.findById(visitId)
                .orElseThrow(() -> new BusinessException("Visita no encontrada", HttpStatus.NOT_FOUND));

        String path = evidenceStorageService.saveBase64Image(dto.imageBase64());
        if (path == null) {
            throw new BusinessException("imageBase64 es obligatorio", HttpStatus.BAD_REQUEST);
        }

        VisitEvidence e = new VisitEvidence(
                null,
                visitId,
                actor.getId(),
                dto.userRole().name().toLowerCase(Locale.ROOT),
                dto.eventType(),
                path,
                dto.latitude(),
                dto.longitude(),
                dto.observation(),
                Instant.now()
        );
        return visitEvidenceRepository.save(e);
    }

    public List<VisitEvidence> listByVisit(Long visitId) {
        visitRepository.findById(visitId)
                .orElseThrow(() -> new BusinessException("Visita no encontrada", HttpStatus.NOT_FOUND));
        return visitEvidenceRepository.findByVisitId(visitId);
    }

    private static long parseLong(String raw, String field) {
        try {
            return Long.parseLong(raw.trim());
        } catch (Exception ex) {
            throw new BusinessException(field + " invalido", HttpStatus.BAD_REQUEST);
        }
    }
}
