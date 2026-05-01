package com.demo.api_gestion_visitas.interfaces.controller;

import com.demo.api_gestion_visitas.application.service.AuthService;
import com.demo.api_gestion_visitas.application.service.VisitService;
import com.demo.api_gestion_visitas.domain.model.Profile;
import com.demo.api_gestion_visitas.domain.model.User;
import com.demo.api_gestion_visitas.domain.model.Visit;
import com.demo.api_gestion_visitas.exception.BusinessException;
import com.demo.api_gestion_visitas.interfaces.dto.ActorPatchRequestDto;
import com.demo.api_gestion_visitas.interfaces.dto.ActorPatchResponseDto;
import com.demo.api_gestion_visitas.interfaces.dto.VisitCreateRequestDto;
import com.demo.api_gestion_visitas.interfaces.dto.VisitEntryRequestDto;
import com.demo.api_gestion_visitas.interfaces.dto.VisitResponseDto;
import com.demo.api_gestion_visitas.interfaces.dto.VisitUpdateRequestDto;
import com.demo.api_gestion_visitas.interfaces.dto.VisitValidationResponseDto;
import com.demo.api_gestion_visitas.interfaces.mapper.VisitDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/visitas")
public class VisitController {
    private final VisitService visitService;
    private final VisitDtoMapper mapper;
    private final AuthService authService;

    public VisitController(VisitService visitService, VisitDtoMapper mapper, AuthService authService) {
        this.visitService = visitService;
        this.mapper = mapper;
        this.authService = authService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COORDINADOR','DIRECTOR')")
    public ResponseEntity<VisitResponseDto> create(Authentication authentication, @Valid @RequestBody VisitCreateRequestDto request) {
        User actor = authService.getByUsername(authentication.getName());
        if (actor.getPerfil() == Profile.COORDINADOR) {
            long cid = Long.parseLong(request.coordinatorId().trim());
            if (!actor.getId().equals(cid)) {
                throw new BusinessException(
                        "Solo puede crear visitas donde usted es el coordinador",
                        HttpStatus.FORBIDDEN
                );
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(visitService.create(request)));
    }

    @GetMapping
    public ResponseEntity<List<VisitResponseDto>> list(Authentication authentication, @RequestParam(required = false) String scope) {
        User actor = authService.getByUsername(authentication.getName());
        List<VisitResponseDto> visits = visitService.listForActor(actor, scope).stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(visitService.findById(id)));
    }

    @GetMapping("/{id}/validation")
    public ResponseEntity<VisitValidationResponseDto> validation(@PathVariable Long id) {
        return ResponseEntity.ok(visitService.validation(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDINADOR','DIRECTOR')")
    public ResponseEntity<VisitResponseDto> update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody VisitUpdateRequestDto request
    ) {
        User actor = authService.getByUsername(authentication.getName());
        if (actor.getPerfil() == Profile.COORDINADOR) {
            Visit ex = visitService.findById(id);
            if (!ex.getCoordinatorId().equals(actor.getId())) {
                throw new BusinessException("No puede editar visitas de otro coordinador", HttpStatus.FORBIDDEN);
            }
        }
        return ResponseEntity.ok(mapper.toResponse(visitService.update(id, request)));
    }

    @PatchMapping("/{id}/promoter-arrival")
    @PreAuthorize("hasRole('PEC')")
    public ResponseEntity<ActorPatchResponseDto> promoterArrival(
            @PathVariable Long id,
            @Valid @RequestBody ActorPatchRequestDto request,
            Authentication authentication
    ) {
        User actor = authService.getByUsername(authentication.getName());
        Visit v = visitService.patchPromoterArrival(id, request, actor);
        return ResponseEntity.ok(new ActorPatchResponseDto(
                String.valueOf(id),
                v.getPromoterArrivalConfirmedAt(),
                "Llegada del promotor registrada",
                null
        ));
    }

    @PatchMapping("/{id}/promoter-close")
    @PreAuthorize("hasRole('PEC')")
    public ResponseEntity<ActorPatchResponseDto> promoterClose(
            @PathVariable Long id,
            @Valid @RequestBody ActorPatchRequestDto request,
            Authentication authentication
    ) {
        User actor = authService.getByUsername(authentication.getName());
        Visit v = visitService.patchPromoterClose(id, request, actor);
        return ResponseEntity.ok(new ActorPatchResponseDto(
                String.valueOf(id),
                v.getPromoterVisitClosedAt(),
                "Cierre del promotor registrado",
                v.isNotificationSentToSupervisor()
        ));
    }

    @PatchMapping("/{id}/coordinator-checkin")
    @PreAuthorize("hasRole('COORDINADOR')")
    public ResponseEntity<ActorPatchResponseDto> coordinatorCheckIn(
            @PathVariable Long id,
            @Valid @RequestBody ActorPatchRequestDto request,
            Authentication authentication
    ) {
        User actor = authService.getByUsername(authentication.getName());
        Visit v = visitService.patchCoordinatorCheckIn(id, request, actor);
        return ResponseEntity.ok(new ActorPatchResponseDto(
                String.valueOf(id),
                v.getCoordinatorCheckInAt(),
                "Check-in del coordinador registrado",
                null
        ));
    }

    @PatchMapping("/{id}/coordinator-close")
    @PreAuthorize("hasRole('COORDINADOR')")
    public ResponseEntity<ActorPatchResponseDto> coordinatorClose(
            @PathVariable Long id,
            @Valid @RequestBody ActorPatchRequestDto request,
            Authentication authentication
    ) {
        User actor = authService.getByUsername(authentication.getName());
        Visit v = visitService.patchCoordinatorClose(id, request, actor);
        return ResponseEntity.ok(new ActorPatchResponseDto(
                String.valueOf(id),
                v.getCoordinatorVisitClosedAt(),
                "Cierre del coordinador registrado",
                v.isNotificationSentToSupervisor()
        ));
    }

    @PostMapping("/{id}/registrar-entrada")
    @PreAuthorize("hasRole('PEC')")
    public ResponseEntity<VisitResponseDto> registerEntry(
            @PathVariable Long id,
            @Valid @RequestBody VisitEntryRequestDto request,
            Authentication authentication
    ) {
        User actor = authService.getByUsername(authentication.getName());
        return ResponseEntity.ok(mapper.toResponse(visitService.registerLegacyEntry(id, request, actor)));
    }
}
