package com.demo.api_gestion_visitas.domain.repository;

import com.demo.api_gestion_visitas.domain.model.Visit;

import java.util.List;
import java.util.Optional;

public interface VisitRepository {
    Visit save(Visit visit);

    Optional<Visit> findById(Long id);

    List<Visit> findAllOrdered();

    List<Visit> findByCoordinatorIdOrdered(Long coordinatorId);

    List<Visit> findByPromoterIdOrdered(Long promoterId);

    List<Visit> findBySupervisorIdOrdered(Long supervisorId);
}
