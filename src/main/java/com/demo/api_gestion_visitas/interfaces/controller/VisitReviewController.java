package com.demo.api_gestion_visitas.interfaces.controller;

import com.demo.api_gestion_visitas.application.service.AuthService;
import com.demo.api_gestion_visitas.application.service.VisitReviewApplicationService;
import com.demo.api_gestion_visitas.domain.model.VisitReview;
import com.demo.api_gestion_visitas.interfaces.dto.VisitReviewCreateRequestDto;
import com.demo.api_gestion_visitas.interfaces.dto.VisitReviewResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visit-reviews")
public class VisitReviewController {
    private final VisitReviewApplicationService visitReviewApplicationService;
    private final AuthService authService;

    public VisitReviewController(VisitReviewApplicationService visitReviewApplicationService, AuthService authService) {
        this.visitReviewApplicationService = visitReviewApplicationService;
        this.authService = authService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<VisitReviewResponseDto> create(Authentication authentication, @Valid @RequestBody VisitReviewCreateRequestDto request) {
        VisitReview r = visitReviewApplicationService.createOrReplace(authService.getByUsername(authentication.getName()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(r));
    }

    @GetMapping("/visit/{visitId}")
    public ResponseEntity<VisitReviewResponseDto> getByVisit(@PathVariable Long visitId) {
        return ResponseEntity.ok(toDto(visitReviewApplicationService.getByVisit(visitId)));
    }

    private VisitReviewResponseDto toDto(VisitReview r) {
        return new VisitReviewResponseDto(
                String.valueOf(r.getId()),
                String.valueOf(r.getVisitId()),
                String.valueOf(r.getSupervisorId()),
                r.getFinalStatus(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}
