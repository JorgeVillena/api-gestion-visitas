package com.demo.api_gestion_visitas.infrastructure.persistence;

import com.demo.api_gestion_visitas.domain.model.VisitReview;
import com.demo.api_gestion_visitas.domain.repository.VisitReviewRepository;
import com.demo.api_gestion_visitas.infrastructure.persistence.entity.VisitReviewEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaVisitReviewRepositoryAdapter implements VisitReviewRepository {
    private final SpringDataVisitReviewRepository repo;

    public JpaVisitReviewRepositoryAdapter(SpringDataVisitReviewRepository repo) {
        this.repo = repo;
    }

    @Override
    public VisitReview save(VisitReview r) {
        VisitReviewEntity e = new VisitReviewEntity();
        e.setId(r.getId());
        e.setVisitId(r.getVisitId());
        e.setSupervisorId(r.getSupervisorId());
        e.setFinalStatus(r.getFinalStatus());
        e.setComment(r.getComment());
        e.setCreatedAt(r.getCreatedAt());
        VisitReviewEntity saved = repo.save(e);
        return toDomain(saved);
    }

    @Override
    public Optional<VisitReview> findByVisitId(Long visitId) {
        return repo.findByVisitId(visitId).map(this::toDomain);
    }

    private VisitReview toDomain(VisitReviewEntity e) {
        return new VisitReview(e.getId(), e.getVisitId(), e.getSupervisorId(), e.getFinalStatus(), e.getComment(), e.getCreatedAt());
    }
}
