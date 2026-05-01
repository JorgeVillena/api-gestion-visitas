package com.demo.api_gestion_visitas.interfaces.controller;

import com.demo.api_gestion_visitas.application.service.AuthService;
import com.demo.api_gestion_visitas.interfaces.dto.AuthResponseDto;
import com.demo.api_gestion_visitas.interfaces.dto.LoginRequestDto;
import com.demo.api_gestion_visitas.interfaces.dto.RegisterRequestDto;
import com.demo.api_gestion_visitas.interfaces.dto.UserResponseDto;
import com.demo.api_gestion_visitas.interfaces.mapper.UserDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserDtoMapper userDtoMapper;

    public AuthController(AuthService authService, UserDtoMapper userDtoMapper) {
        this.authService = authService;
        this.userDtoMapper = userDtoMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userDtoMapper.toResponse(authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(Authentication authentication) {
        return ResponseEntity.ok(userDtoMapper.toResponse(authService.getByUsername(authentication.getName())));
    }
}
