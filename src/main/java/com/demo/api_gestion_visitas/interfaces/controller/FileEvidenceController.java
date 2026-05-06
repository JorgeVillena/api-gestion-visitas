package com.demo.api_gestion_visitas.interfaces.controller;

import com.demo.api_gestion_visitas.infrastructure.storage.EvidenceStorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files/evidencias")
public class FileEvidenceController {
    private final EvidenceStorageService evidenceStorageService;

    public FileEvidenceController(EvidenceStorageService evidenceStorageService) {
        this.evidenceStorageService = evidenceStorageService;
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getEvidence(@PathVariable String fileName) {
        return evidenceStorageService.resolveStoredEvidence(fileName)
                .map(path -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body((Resource) new FileSystemResource(path)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
