package com.demo.api_gestion_visitas.domain.repository;

import com.demo.api_gestion_visitas.domain.model.VisitEvidence;

import java.util.List;

public interface VisitEvidenceRepository {
    VisitEvidence save(VisitEvidence evidence);

    List<VisitEvidence> findByVisitId(Long visitId);
}
