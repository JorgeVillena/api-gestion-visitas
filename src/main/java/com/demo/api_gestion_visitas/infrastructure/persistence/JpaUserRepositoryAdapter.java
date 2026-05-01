package com.demo.api_gestion_visitas.infrastructure.persistence;

import com.demo.api_gestion_visitas.domain.model.User;
import com.demo.api_gestion_visitas.domain.model.Profile;
import com.demo.api_gestion_visitas.domain.repository.UserRepository;
import com.demo.api_gestion_visitas.infrastructure.persistence.mapper.UserPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;
    private final UserPersistenceMapper mapper;

    public JpaUserRepositoryAdapter(SpringDataUserRepository springDataUserRepository, UserPersistenceMapper mapper) {
        this.springDataUserRepository = springDataUserRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(springDataUserRepository.save(mapper.toEntity(user)));
    }

    @Override
    public Optional<User> findByUsuario(String usuario) {
        return springDataUserRepository.findByUsuario(usuario).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return springDataUserRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsuario(String usuario) {
        return springDataUserRepository.existsByUsuario(usuario);
    }

    @Override
    public List<User> findByPerfil(Profile perfil) {
        return springDataUserRepository.findByPerfil(perfil).stream().map(mapper::toDomain).toList();
    }
}
