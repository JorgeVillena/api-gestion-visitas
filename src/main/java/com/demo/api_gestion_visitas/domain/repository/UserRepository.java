package com.demo.api_gestion_visitas.domain.repository;

import com.demo.api_gestion_visitas.domain.model.User;
import com.demo.api_gestion_visitas.domain.model.Profile;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findByUsuario(String usuario);

    Optional<User> findById(Long id);

    boolean existsByUsuario(String usuario);

    List<User> findByPerfil(Profile perfil);
}
