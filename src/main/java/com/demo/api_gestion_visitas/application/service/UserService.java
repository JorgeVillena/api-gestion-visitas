package com.demo.api_gestion_visitas.application.service;

import com.demo.api_gestion_visitas.domain.model.Profile;
import com.demo.api_gestion_visitas.domain.model.User;
import com.demo.api_gestion_visitas.domain.repository.UserRepository;
import com.demo.api_gestion_visitas.exception.BusinessException;
import com.demo.api_gestion_visitas.interfaces.dto.UserUpdateRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User update(Long id, UserUpdateRequestDto request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        User updated = new User(
                existing.getId(),
                request.nombres(),
                request.apellidos(),
                existing.getUsuario(),
                existing.getPasswordHash(),
                request.perfil(),
                coalesce(request.schoolName(), existing.getSchoolName()),
                coalesce(request.modularCode(), existing.getModularCode()),
                coalesce(request.ugelName(), existing.getUgelName()),
                coalesce(request.locationName(), existing.getLocationName()),
                coalesce(request.documentNumber(), existing.getDocumentNumber()),
                request.birthDate() != null ? request.birthDate() : existing.getBirthDate()
        );
        return userRepository.save(updated);
    }

    public List<User> listPromotores() {
        return userRepository.findByPerfil(Profile.PEC);
    }

    private static String coalesce(String a, String b) {
        return a != null ? a : b;
    }
}
