package com.demo.api_gestion_visitas.infrastructure.persistence;

import com.demo.api_gestion_visitas.domain.model.VisitEvidence;
import com.demo.api_gestion_visitas.domain.repository.VisitEvidenceRepository;
import com.demo.api_gestion_visitas.infrastructure.persistence.entity.VisitEvidenceEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaVisitEvidenceRepositoryAdapter implements VisitEvidenceRepository {
    private final SpringDataVisitEvidenceRepository repo;

    public JpaVisitEvidenceRepositoryAdapter(SpringDataVisitEvidenceRepository repo) {
        this.repo = repo;
    }

    @Override
    public VisitEvidence save(VisitEvidence e) {
        VisitEvidenceEntity entity = toEntity(e);
        return toDomain(repo.save(entity));
    }

    @Override
    public List<VisitEvidence> findByVisitId(Long visitId) {
        return repo.findByVisitIdOrderByCreatedAtAsc(visitId).stream().map(this::toDomain).toList();
    }

    private VisitEvidenceEntity toEntity(VisitEvidence e) {
        VisitEvidenceEntity x = new VisitEvidenceEntity();
        x.setId(e.getId());
        x.setVisitId(e.getVisitId());
        x.setUserId(e.getUserId());
        x.setUserRole(e.getUserRole());
        x.setEventType(e.getEventType());
        x.setImageUrl(e.getImageUrl());
        x.setLatitude(e.getLatitude());
        x.setLongitude(e.getLongitude());
        x.setObservation(e.getObservation());
        x.setCreatedAt(e.getCreatedAt());
        return x;
    }

    private VisitEvidence toDomain(VisitEvidenceEntity x) {
        return new VisitEvidence(
                x.getId(),
                x.getVisitId(),
                x.getUserId(),
                x.getUserRole(),
                x.getEventType(),
                x.getImageUrl(),
                x.getLatitude(),
                x.getLongitude(),
                x.getObservation(),
                x.getCreatedAt()
        );
    }
}
