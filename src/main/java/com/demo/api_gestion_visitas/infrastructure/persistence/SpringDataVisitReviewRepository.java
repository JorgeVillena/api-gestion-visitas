package com.demo.api_gestion_visitas.infrastructure.persistence;

import com.demo.api_gestion_visitas.infrastructure.persistence.entity.VisitReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataVisitReviewRepository extends JpaRepository<VisitReviewEntity, Long> {
    Optional<VisitReviewEntity> findByVisitId(Long visitId);
}
