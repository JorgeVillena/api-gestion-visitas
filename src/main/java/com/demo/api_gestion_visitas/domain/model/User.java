package com.demo.api_gestion_visitas.domain.model;

import java.time.LocalDate;

public class User {
    private final Long id;
    private final String nombres;
    private final String apellidos;
    private final String usuario;
    private final String passwordHash;
    private final Profile perfil;
    private final String schoolName;
    private final String modularCode;
    private final String ugelName;
    private final String locationName;
    private final String documentNumber;
    private final LocalDate birthDate;

    public User(
            Long id,
            String nombres,
            String apellidos,
            String usuario,
            String passwordHash,
            Profile perfil,
            String schoolName,
            String modularCode,
            String ugelName,
            String locationName,
            String documentNumber,
            LocalDate birthDate
    ) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.usuario = usuario;
        this.passwordHash = passwordHash;
        this.perfil = perfil;
        this.schoolName = schoolName;
        this.modularCode = modularCode;
        this.ugelName = ugelName;
        this.locationName = locationName;
        this.documentNumber = documentNumber;
        this.birthDate = birthDate;
    }

    public Long getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Profile getPerfil() {
        return perfil;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getModularCode() {
        return modularCode;
    }

    public String getUgelName() {
        return ugelName;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String fullName() {
        return (nombres != null ? nombres : "").trim() + " " + (apellidos != null ? apellidos : "").trim();
    }
}
