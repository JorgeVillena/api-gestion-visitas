package com.demo.api_gestion_visitas;

import com.demo.api_gestion_visitas.infrastructure.persistence.SpringDataUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringDataUserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    @Test
    void registerSuccess() throws Exception {
        var payload = """
                {
                  "nombres": "Jorge",
                  "apellidos": "Villena",
                  "usuario": "jorgev",
                  "password": "secret123",
                  "perfil": "ESPECIALISTA"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.usuario").value("jorgev"))
                .andExpect(jsonPath("$.perfil").value("ESPECIALISTA"));
    }

    @Test
    void registerDuplicateUserReturnsConflict() throws Exception {
        String payload = """
                {
                  "nombres": "Jorge",
                  "apellidos": "Villena",
                  "usuario": "jorgev",
                  "password": "secret123",
                  "perfil": "ESPECIALISTA"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El usuario ya existe"));
    }

    @Test
    void loginSuccessReturnsTokenAndUserData() throws Exception {
        registerDefaultUser();

        var loginPayload = """
                {
                  "usuario": "jorgev",
                  "password": "secret123",
                  "perfil": "ESPECIALISTA"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.nombres").value("Jorge"))
                .andExpect(jsonPath("$.username").value("jorgev"))
                .andExpect(jsonPath("$.perfil").value("ESPECIALISTA"));
    }

    @Test
    void loginInvalidProfileReturnsUnauthorized() throws Exception {
        registerDefaultUser();

        var loginPayload = """
                {
                  "usuario": "jorgev",
                  "password": "secret123",
                  "perfil": "DIRECTOR"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales invalidas"));
    }

    private void registerDefaultUser() throws Exception {
        var payload = objectMapper.writeValueAsString(new RegisterRequest("Jorge", "Villena", "jorgev", "secret123", "ESPECIALISTA"));
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());
    }

    private record RegisterRequest(String nombres, String apellidos, String usuario, String password, String perfil) {
    }
}
