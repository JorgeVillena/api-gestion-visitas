package com.demo.api_gestion_visitas.infrastructure.storage;

import com.demo.api_gestion_visitas.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class EvidenceStorageService {
    private final Path evidencesDir;

    public EvidenceStorageService(@Value("${app.storage.evidencias-dir:./data/evidencias}") String evidencesDir) {
        this.evidencesDir = Paths.get(evidencesDir);
    }

    public String saveBase64Image(String evidenceBase64) {
        if (evidenceBase64 == null || evidenceBase64.isBlank()) {
            return null;
        }

        String sanitizedBase64 = stripDataUrlPrefix(evidenceBase64);
        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(sanitizedBase64);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Formato de evidenciaBase64 invalido", HttpStatus.BAD_REQUEST);
        }

        try {
            Files.createDirectories(evidencesDir);
            String fileName = "evidencia_" + UUID.randomUUID() + ".jpg";
            Path filePath = evidencesDir.resolve(fileName);
            Files.write(filePath, imageBytes);
            return filePath.toString().replace("\\", "/");
        } catch (IOException ex) {
            throw new BusinessException("No se pudo guardar la evidencia", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String stripDataUrlPrefix(String input) {
        int commaIndex = input.indexOf(',');
        if (input.startsWith("data:") && commaIndex > -1) {
            return input.substring(commaIndex + 1);
        }
        return input;
    }
}
