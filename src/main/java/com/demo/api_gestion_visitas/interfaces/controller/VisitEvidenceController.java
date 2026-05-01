package com.demo.api_gestion_visitas.interfaces.controller;

import com.demo.api_gestion_visitas.application.service.AuthService;
import com.demo.api_gestion_visitas.application.service.VisitEvidenceApplicationService;
import com.demo.api_gestion_visitas.domain.model.VisitEvidence;
import com.demo.api_gestion_visitas.interfaces.dto.VisitEvidenceCreateRequestDto;
import com.demo.api_gestion_visitas.interfaces.dto.VisitEvidenceResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/visit-evidences")
public class VisitEvidenceController {
    private final VisitEvidenceApplicationService visitEvidenceApplicationService;
    private final AuthService authService;

    public VisitEvidenceController(VisitEvidenceApplicationService visitEvidenceApplicationService, AuthService authService) {
        this.visitEvidenceApplicationService = visitEvidenceApplicationService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<VisitEvidenceResponseDto> create(Authentication authentication, @Valid @RequestBody VisitEvidenceCreateRequestDto request) {
        VisitEvidence e = visitEvidenceApplicationService.create(authService.getByUsername(authentication.getName()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(e));
    }

    @GetMapping("/visit/{visitId}")
    public ResponseEntity<List<VisitEvidenceResponseDto>> listByVisit(@PathVariable Long visitId) {
        return ResponseEntity.ok(visitEvidenceApplicationService.listByVisit(visitId).stream().map(this::toDto).toList());
    }

    private VisitEvidenceResponseDto toDto(VisitEvidence e) {
        return new VisitEvidenceResponseDto(
                String.valueOf(e.getId()),
                String.valueOf(e.getVisitId()),
                String.valueOf(e.getUserId()),
                e.getUserRole(),
                e.getEventType(),
                e.getImageUrl(),
                e.getLatitude(),
                e.getLongitude(),
                e.getObservation(),
                e.getCreatedAt()
        );
    }
}
