package com.demo.api_gestion_visitas;

import com.demo.api_gestion_visitas.infrastructure.persistence.SpringDataDeviceTokenRepository;
import com.demo.api_gestion_visitas.infrastructure.persistence.SpringDataUserRepository;
import com.demo.api_gestion_visitas.infrastructure.persistence.SpringDataVisitEvidenceRepository;
import com.demo.api_gestion_visitas.infrastructure.persistence.SpringDataVisitRepository;
import com.demo.api_gestion_visitas.infrastructure.persistence.SpringDataVisitReviewRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DirectorAndEvidenceIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringDataVisitRepository visitRepository;

    @Autowired
    private SpringDataUserRepository userRepository;

    @Autowired
    private SpringDataVisitEvidenceRepository visitEvidenceRepository;

    @Autowired
    private SpringDataVisitReviewRepository visitReviewRepository;

    @Autowired
    private SpringDataDeviceTokenRepository deviceTokenRepository;

    @BeforeEach
    void cleanDb() {
        visitEvidenceRepository.deleteAll();
        visitReviewRepository.deleteAll();
        deviceTokenRepository.deleteAll();
        visitRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void directorOverviewAndFcmAndEvidenceFlow() throws Exception {
        register("dir1", "DIRECTOR");
        register("coordD", "COORDINADOR");
        register("pecD", "PEC");
        register("supD", "ESPECIALISTA");

        String dirToken = login("dir1", "DIRECTOR");
        String coordToken = login("coordD", "COORDINADOR");
        long coordId = loginId("coordD", "COORDINADOR");
        long pecId = loginId("pecD", "PEC");
        long supId = loginId("supD", "ESPECIALISTA");

        mockMvc.perform(post("/devices/fcm-token")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + login("supD", "ESPECIALISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"demo-fcm-token\"}"))
                .andExpect(status().isNoContent());

        String createVisit = """
                {
                  "coordinatorId": "%d",
                  "promoterId": "%d",
                  "supervisorId": "%d",
                  "placeName": "IE Demo",
                  "scheduledDate": "2026-05-10",
                  "expectedStartTime": "08:00",
                  "expectedEndTime": "10:00",
                  "latitude": -10.0,
                  "longitude": -75.0
                }
                """.formatted(coordId, pecId, supId);

        MvcResult visitRes = mockMvc.perform(post("/visitas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + coordToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createVisit))
                .andExpect(status().isCreated())
                .andReturn();
        long visitId = Long.parseLong(objectMapper.readTree(visitRes.getResponse().getContentAsString()).get("id").asText());

        String evidenceBody = """
                {
                  "visitId": "%d",
                  "imageBase64": "aGVsbG8=",
                  "latitude": -10.0,
                  "longitude": -75.0,
                  "observation": "Foto",
                  "userRole": "PEC",
                  "eventType": "MANUAL"
                }
                """.formatted(visitId);

        mockMvc.perform(post("/visit-evidences")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + login("pecD", "PEC"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(evidenceBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.visitId").value(String.valueOf(visitId)));

        mockMvc.perform(get("/visit-evidences/visit/{visitId}", visitId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + coordToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/director/overview")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + dirToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVisitasProgramadas").exists());

        mockMvc.perform(get("/director/reports")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + dirToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visitas").isArray());
    }

    @Test
    void visitReviewCreateAndGet() throws Exception {
        register("coordR", "COORDINADOR");
        register("pecR", "PEC");
        register("supR", "ESPECIALISTA");

        String coordToken = login("coordR", "COORDINADOR");
        long coordId = loginId("coordR", "COORDINADOR");
        long pecId = loginId("pecR", "PEC");
        long supId = loginId("supR", "ESPECIALISTA");

        String createVisit = """
                {
                  "coordinatorId": "%d",
                  "promoterId": "%d",
                  "supervisorId": "%d",
                  "placeName": "IE R",
                  "scheduledDate": "2026-05-11",
                  "expectedStartTime": "09:00",
                  "expectedEndTime": "11:00",
                  "latitude": -10.0,
                  "longitude": -75.0
                }
                """.formatted(coordId, pecId, supId);

        MvcResult visitRes = mockMvc.perform(post("/visitas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + coordToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createVisit))
                .andExpect(status().isCreated())
                .andReturn();
        long visitId = Long.parseLong(objectMapper.readTree(visitRes.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(get("/visit-reviews/visit/{visitId}", visitId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + login("supR", "ESPECIALISTA")))
                .andExpect(status().isNotFound());

        String reviewBody = """
                {
                  "visitId": "%d",
                  "finalStatus": "Conforme",
                  "comment": "Ok"
                }
                """.formatted(visitId);

        mockMvc.perform(post("/visit-reviews")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + login("supR", "ESPECIALISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.finalStatus").value("Conforme"));

        mockMvc.perform(get("/visit-reviews/visit/{visitId}", visitId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + login("supR", "ESPECIALISTA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalStatus").value("Conforme"));
    }

    private void register(String usuario, String perfil) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombres": "N",
                                  "apellidos": "A",
                                  "usuario": "%s",
                                  "password": "secret123",
                                  "perfil": "%s"
                                }
                                """.formatted(usuario, perfil)))
                .andExpect(status().isCreated());
    }

    private String login(String usuario, String perfil) throws Exception {
        MvcResult r = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuario": "%s",
                                  "password": "secret123",
                                  "perfil": "%s"
                                }
                                """.formatted(usuario, perfil)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("token").asText();
    }

    private long loginId(String usuario, String perfil) throws Exception {
        MvcResult r = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuario": "%s",
                                  "password": "secret123",
                                  "perfil": "%s"
                                }
                                """.formatted(usuario, perfil)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode b = objectMapper.readTree(r.getResponse().getContentAsString());
        return b.get("id").asLong();
    }
}
