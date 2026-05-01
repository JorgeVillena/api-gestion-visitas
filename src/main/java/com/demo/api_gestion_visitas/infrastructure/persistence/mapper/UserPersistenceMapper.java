package com.demo.api_gestion_visitas.infrastructure.persistence.mapper;

import com.demo.api_gestion_visitas.domain.model.User;
import com.demo.api_gestion_visitas.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {
    public UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setNombres(user.getNombres());
        entity.setApellidos(user.getApellidos());
        entity.setUsuario(user.getUsuario());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setPerfil(user.getPerfil());
        entity.setSchoolName(user.getSchoolName());
        entity.setModularCode(user.getModularCode());
        entity.setUgelName(user.getUgelName());
        entity.setLocationName(user.getLocationName());
        entity.setDocumentNumber(user.getDocumentNumber());
        entity.setBirthDate(user.getBirthDate());
        return entity;
    }

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getNombres(),
                entity.getApellidos(),
                entity.getUsuario(),
                entity.getPasswordHash(),
                entity.getPerfil(),
                entity.getSchoolName(),
                entity.getModularCode(),
                entity.getUgelName(),
                entity.getLocationName(),
                entity.getDocumentNumber(),
                entity.getBirthDate()
        );
    }
}
