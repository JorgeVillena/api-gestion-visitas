package com.demo.api_gestion_visitas.domain.repository;

import com.demo.api_gestion_visitas.domain.model.VisitReview;

import java.util.Optional;

public interface VisitReviewRepository {
    VisitReview save(VisitReview review);

    Optional<VisitReview> findByVisitId(Long visitId);
}
