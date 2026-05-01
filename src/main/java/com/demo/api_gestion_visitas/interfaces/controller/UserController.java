package com.demo.api_gestion_visitas.interfaces.controller;

import com.demo.api_gestion_visitas.application.service.UserService;
import com.demo.api_gestion_visitas.interfaces.dto.UserResponseDto;
import com.demo.api_gestion_visitas.interfaces.dto.UserUpdateRequestDto;
import com.demo.api_gestion_visitas.interfaces.mapper.UserDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    public UserController(UserService userService, UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequestDto request) {
        return ResponseEntity.ok(userDtoMapper.toResponse(userService.update(id, request)));
    }

    @GetMapping({"/promotores", "/profesores"})
    @PreAuthorize("hasAnyRole('COORDINADOR','ESPECIALISTA','DIRECTOR')")
    public ResponseEntity<List<UserResponseDto>> listPromotores() {
        List<UserResponseDto> list = userService.listPromotores().stream().map(userDtoMapper::toResponse).toList();
        return ResponseEntity.ok(list);
    }
}
