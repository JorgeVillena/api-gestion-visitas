package com.demo.api_gestion_visitas.interfaces.mapper;

import com.demo.api_gestion_visitas.domain.model.User;
import com.demo.api_gestion_visitas.interfaces.dto.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
    public UserResponseDto toResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getNombres(),
                user.getApellidos(),
                user.getUsuario(),
                user.getPerfil(),
                user.fullName().trim(),
                user.getSchoolName(),
                user.getModularCode(),
                user.getUgelName(),
                user.getLocationName(),
                user.getDocumentNumber(),
                user.getBirthDate()
        );
    }
}
