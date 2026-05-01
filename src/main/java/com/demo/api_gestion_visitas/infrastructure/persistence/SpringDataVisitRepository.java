package com.demo.api_gestion_visitas.infrastructure.persistence;

import com.demo.api_gestion_visitas.infrastructure.persistence.entity.VisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataVisitRepository extends JpaRepository<VisitEntity, Long> {
    List<VisitEntity> findAllByOrderByScheduledDateAscExpectedStartTimeAsc();

    List<VisitEntity> findByCoordinatorIdOrderByScheduledDateAscExpectedStartTimeAsc(Long coordinatorId);

    List<VisitEntity> findByPromoterIdOrderByScheduledDateAscExpectedStartTimeAsc(Long promoterId);

    List<VisitEntity> findBySupervisorIdOrderByScheduledDateAscExpectedStartTimeAsc(Long supervisorId);
}
