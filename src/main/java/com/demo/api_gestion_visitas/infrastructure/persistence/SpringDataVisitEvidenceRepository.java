package com.demo.api_gestion_visitas.infrastructure.persistence;

import com.demo.api_gestion_visitas.infrastructure.persistence.entity.VisitEvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataVisitEvidenceRepository extends JpaRepository<VisitEvidenceEntity, Long> {
    List<VisitEvidenceEntity> findByVisitIdOrderByCreatedAtAsc(Long visitId);
}
