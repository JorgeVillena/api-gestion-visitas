package com.demo.api_gestion_visitas.application.service;

import com.demo.api_gestion_visitas.domain.model.Profile;
import com.demo.api_gestion_visitas.domain.model.User;
import com.demo.api_gestion_visitas.domain.model.VisitReview;
import com.demo.api_gestion_visitas.domain.repository.VisitRepository;
import com.demo.api_gestion_visitas.domain.repository.VisitReviewRepository;
import com.demo.api_gestion_visitas.exception.BusinessException;
import com.demo.api_gestion_visitas.interfaces.dto.VisitReviewCreateRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Service
public class VisitReviewApplicationService {
    private final VisitRepository visitRepository;
    private final VisitReviewRepository visitReviewRepository;

    public VisitReviewApplicationService(VisitRepository visitRepository, VisitReviewRepository visitReviewRepository) {
        this.visitRepository = visitRepository;
        this.visitReviewRepository = visitReviewRepository;
    }

    public VisitReview createOrReplace(User supervisor, VisitReviewCreateRequestDto dto) {
        if (supervisor.getPerfil() != Profile.ESPECIALISTA) {
            throw new BusinessException("Solo especialistas pueden registrar revision", HttpStatus.FORBIDDEN);
        }
        long visitId = parseLong(dto.visitId(), "visitId");
        var visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new BusinessException("Visita no encontrada", HttpStatus.NOT_FOUND));
        if (!visit.getSupervisorId().equals(supervisor.getId())) {
            throw new BusinessException("El supervisor no esta asignado a esta visita", HttpStatus.FORBIDDEN);
        }
        String finalStatus = normalizeFinalStatus(dto.finalStatus());

        Optional<VisitReview> prev = visitReviewRepository.findByVisitId(visitId);
        VisitReview toSave = new VisitReview(
                prev.map(VisitReview::getId).orElse(null),
                visitId,
                supervisor.getId(),
                finalStatus,
                dto.comment(),
                Instant.now()
        );
        return visitReviewRepository.save(toSave);
    }

    public VisitReview getByVisit(Long visitId) {
        visitRepository.findById(visitId)
                .orElseThrow(() -> new BusinessException("Visita no encontrada", HttpStatus.NOT_FOUND));
        return visitReviewRepository.findByVisitId(visitId)
                .orElseThrow(() -> new BusinessException("Revision no encontrada", HttpStatus.NOT_FOUND));
    }

    private static String normalizeFinalStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BusinessException("finalStatus es obligatorio", HttpStatus.BAD_REQUEST);
        }
        String s = raw.trim();
        String lower = s.toLowerCase(Locale.ROOT);
        if (lower.equals("conforme")) {
            return "Conforme";
        }
        if (lower.equals("observada")) {
            return "Observada";
        }
        throw new BusinessException("finalStatus debe ser Conforme u Observada", HttpStatus.BAD_REQUEST);
    }

    private static long parseLong(String raw, String field) {
        try {
            return Long.parseLong(raw.trim());
        } catch (Exception ex) {
            throw new BusinessException(field + " invalido", HttpStatus.BAD_REQUEST);
        }
    }
}
