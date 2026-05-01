package com.demo.api_gestion_visitas.application.service;

import com.demo.api_gestion_visitas.domain.model.User;
import com.demo.api_gestion_visitas.domain.repository.UserRepository;
import com.demo.api_gestion_visitas.exception.BusinessException;
import com.demo.api_gestion_visitas.infrastructure.security.JwtTokenProvider;
import com.demo.api_gestion_visitas.interfaces.dto.AuthResponseDto;
import com.demo.api_gestion_visitas.interfaces.dto.LoginRequestDto;
import com.demo.api_gestion_visitas.interfaces.dto.RegisterRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User register(RegisterRequestDto request) {
        if (userRepository.existsByUsuario(request.usuario())) {
            throw new BusinessException("El usuario ya existe", HttpStatus.CONFLICT);
        }

        User user = new User(
                null,
                request.nombres(),
                request.apellidos(),
                request.usuario(),
                passwordEncoder.encode(request.password()),
                request.perfil(),
                request.schoolName(),
                request.modularCode(),
                request.ugelName(),
                request.locationName(),
                request.documentNumber(),
                request.birthDate()
        );
        return userRepository.save(user);
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsuario(request.usuario())
                .orElseThrow(() -> new BusinessException("Credenciales invalidas", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash()) || user.getPerfil() != request.perfil()) {
            throw new BusinessException("Credenciales invalidas", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtTokenProvider.generateToken(user.getUsuario(), user.getPerfil());
        return toAuthResponse(token, user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsuario(username)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado", HttpStatus.NOT_FOUND));
    }

    private AuthResponseDto toAuthResponse(String token, User user) {
        return new AuthResponseDto(
                token,
                user.getId(),
                user.getUsuario(),
                user.fullName().trim(),
                user.getPerfil(),
                user.getNombres(),
                user.getApellidos(),
                user.getPerfil(),
                user.getSchoolName(),
                user.getModularCode(),
                user.getUgelName(),
                user.getLocationName(),
                user.getDocumentNumber(),
                user.getBirthDate()
        );
    }
}
