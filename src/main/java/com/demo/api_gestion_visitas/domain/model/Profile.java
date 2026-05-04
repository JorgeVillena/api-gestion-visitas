package com.demo.api_gestion_visitas.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * Perfiles oficiales de la app (requerimiento v3). No existe {@code PROFESOR} ni rol separado {@code ADMIN}
 * (uso histórico de admin se trata como especialista/supervisor).
 * <p>
 * <strong>Negocio → valor JSON canónico (JWT y persistencia)</strong>
 * <ul>
 *   <li>PEC / Promotor → {@code PEC}</li>
 *   <li>Coordinador → {@code COORDINADOR}</li>
 *   <li>Especialista / Supervisor → {@code ESPECIALISTA}</li>
 *   <li>Director → {@code DIRECTOR}</li>
 * </ul>
 * En <strong>entrada</strong> JSON se aceptan alias: {@code PROMOTOR}→PEC, {@code SUPERVISOR}→ESPECIALISTA,
 * {@code ADMIN}→ESPECIALISTA. {@code PROFESOR} se rechaza con error claro.
 */
public enum Profile {
    PEC,
    COORDINADOR,
    ESPECIALISTA,
    DIRECTOR;

    @JsonValue
    public String toApi() {
        return name();
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Profile fromApi(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String v = raw.trim();
        String u = v.toUpperCase(Locale.ROOT);
        return switch (u) {
            case "PEC", "PROMOTOR", "PEC_PROMOTOR", "PEC-PROMOTOR", "PEC/PROMOTOR" -> PEC;
            case "COORDINADOR" -> COORDINADOR;
            case "ESPECIALISTA", "SUPERVISOR" -> ESPECIALISTA;
            case "DIRECTOR" -> DIRECTOR;
            case "ADMIN" -> ESPECIALISTA;
            case "PROFESOR" -> throw new IllegalArgumentException(
                    "El rol PROFESOR no está soportado. Use PEC (promotor), COORDINADOR, ESPECIALISTA (supervisor) o DIRECTOR.");
            default -> {
                try {
                    yield Profile.valueOf(u);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("perfil no reconocido: " + v);
                }
            }
        };
    }

    /**
     * Rol para Spring Security ({@code hasRole('…')}) y claims JWT; alinea tokens antiguos con {@code ADMIN}.
     */
    public static String toSecurityRoleName(String perfilClaim) {
        if (perfilClaim == null || perfilClaim.isBlank()) {
            return "";
        }
        return switch (perfilClaim.trim()) {
            case "ADMIN" -> ESPECIALISTA.name();
            default -> perfilClaim.trim();
        };
    }
}
