package com.demo.api_gestion_visitas.infrastructure.persistence;

import com.demo.api_gestion_visitas.domain.model.Visit;
import com.demo.api_gestion_visitas.domain.repository.VisitRepository;
import com.demo.api_gestion_visitas.infrastructure.persistence.mapper.VisitPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaVisitRepositoryAdapter implements VisitRepository {
    private final SpringDataVisitRepository springDataVisitRepository;
    private final VisitPersistenceMapper mapper;

    public JpaVisitRepositoryAdapter(SpringDataVisitRepository springDataVisitRepository, VisitPersistenceMapper mapper) {
        this.springDataVisitRepository = springDataVisitRepository;
        this.mapper = mapper;
    }

    @Override
    public Visit save(Visit visit) {
        return mapper.toDomain(springDataVisitRepository.save(mapper.toEntity(visit)));
    }

    @Override
    public Optional<Visit> findById(Long id) {
        return springDataVisitRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Visit> findAllOrdered() {
        return springDataVisitRepository.findAllByOrderByScheduledDateAscExpectedStartTimeAsc().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Visit> findByCoordinatorIdOrdered(Long coordinatorId) {
        return springDataVisitRepository.findByCoordinatorIdOrderByScheduledDateAscExpectedStartTimeAsc(coordinatorId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Visit> findByPromoterIdOrdered(Long promoterId) {
        return springDataVisitRepository.findByPromoterIdOrderByScheduledDateAscExpectedStartTimeAsc(promoterId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Visit> findBySupervisorIdOrdered(Long supervisorId) {
        return springDataVisitRepository.findBySupervisorIdOrderByScheduledDateAscExpectedStartTimeAsc(supervisorId).stream().map(mapper::toDomain).toList();
    }
}
