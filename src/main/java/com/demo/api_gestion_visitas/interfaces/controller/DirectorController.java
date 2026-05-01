package com.demo.api_gestion_visitas.interfaces.controller;

import com.demo.api_gestion_visitas.application.service.DirectorApplicationService;
import com.demo.api_gestion_visitas.exception.BusinessException;
import com.demo.api_gestion_visitas.interfaces.dto.DirectorOverviewResponseDto;
import com.demo.api_gestion_visitas.interfaces.dto.DirectorReportResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/director")
public class DirectorController {
    private final DirectorApplicationService directorApplicationService;

    public DirectorController(DirectorApplicationService directorApplicationService) {
        this.directorApplicationService = directorApplicationService;
    }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<DirectorOverviewResponseDto> overview() {
        return ResponseEntity.ok(directorApplicationService.overview());
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<DirectorReportResponseDto> reports(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) String coordinatorId
    ) {
        Long coordFilter = null;
        if (coordinatorId != null && !coordinatorId.isBlank()) {
            try {
                coordFilter = Long.parseLong(coordinatorId.trim());
            } catch (NumberFormatException ex) {
                throw new BusinessException("coordinatorId invalido", HttpStatus.BAD_REQUEST);
            }
        }
        return ResponseEntity.ok(directorApplicationService.report(from, to, coordFilter));
    }
}
