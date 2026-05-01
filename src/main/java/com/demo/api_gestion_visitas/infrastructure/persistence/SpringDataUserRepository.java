package com.demo.api_gestion_visitas.infrastructure.persistence;

import com.demo.api_gestion_visitas.infrastructure.persistence.entity.UserEntity;
import com.demo.api_gestion_visitas.domain.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsuario(String usuario);

    boolean existsByUsuario(String usuario);

    List<UserEntity> findByPerfil(Profile perfil);
}
