package com.demo.api_gestion_visitas;

import com.demo.api_gestion_visitas.infrastructure.persistence.SpringDataUserRepository;
import com.demo.api_gestion_visitas.infrastructure.persistence.SpringDataVisitRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VisitIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringDataVisitRepository visitRepository;

    @Autowired
    private SpringDataUserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        visitRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createVisitSuccessfully() throws Exception {
        String coordToken = authenticateAndGetToken("coord1", "COORDINADOR");
        long coordId = userIdFor("coord1");
        long promoterId = registerUser("pec1", "PEC");
        long supervisorId = registerUser("esp1", "ESPECIALISTA");

        String payload = visitCreateJson(coordId, promoterId, supervisorId, "Aula 201", "2026-05-01", "08:00", "10:00", -10.5, -75.4);

        mockMvc.perform(post("/visitas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + coordToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.promoterId").value(String.valueOf(promoterId)))
                .andExpect(jsonPath("$.placeName").value("Aula 201"));
    }

    @Test
    void listVisitsSuccessfully() throws Exception {
        String coordToken = authenticateAndGetToken("coord2", "COORDINADOR");
        long coordId = userIdFor("coord2");
        long pecA = registerUser("pecA", "PEC");
        long pecB = registerUser("pecB", "PEC");
        long sup = registerUser("sup1", "ESPECIALISTA");

        createVisit(coordToken, coordId, pecB, sup, "Laboratorio", "2026-05-01", "09:00", "11:00");
        createVisit(coordToken, coordId, pecA, sup, "Aula 101", "2026-05-01", "08:00", "10:00");

        mockMvc.perform(get("/visitas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + coordToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].promoterId").value(String.valueOf(pecA)))
                .andExpect(jsonPath("$[1].promoterId").value(String.valueOf(pecB)));
    }

    @Test
    void getVisitByIdSuccessfully() throws Exception {
        String coordToken = authenticateAndGetToken("coord3", "COORDINADOR");
        long coordId = userIdFor("coord3");
        long promoterId = registerUser("pec3", "PEC");
        long supervisorId = registerUser("sup2", "ESPECIALISTA");
        long id = createVisit(coordToken, coordId, promoterId, supervisorId, "Biblioteca", "2026-05-01", "10:00", "12:00");

        mockMvc.perform(get("/visitas/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + coordToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(String.valueOf(id)))
                .andExpect(jsonPath("$.placeName").value("Biblioteca"));
    }

    @Test
    void updateVisitSuccessfully() throws Exception {
        String coordToken = authenticateAndGetToken("coord4", "COORDINADOR");
        long coordId = userIdFor("coord4");
        long pec1 = registerUser("pecUp1", "PEC");
        long pec2 = registerUser("pecUp2", "PEC");
        long sup = registerUser("sup3", "ESPECIALISTA");
        long id = createVisit(coordToken, coordId, pec1, sup, "Aula 103", "2026-05-01", "11:00", "12:00");

        String payload = visitUpdateJson(coordId, pec2, sup, "Aula 300", "2026-05-01", "12:30", "13:30", -10.5, -75.4, null);

        mockMvc.perform(put("/visitas/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + coordToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promoterId").value(String.valueOf(pec2)))
                .andExpect(jsonPath("$.placeName").value("Aula 300"));
    }

    @Test
    void getVisitNotFoundReturns404() throws Exception {
        String token = authenticateAndGetToken("coord5", "COORDINADOR");
        mockMvc.perform(get("/visitas/{id}", 99999)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Visita no encontrada"));
    }

    @Test
    void updateVisitNotFoundReturns404() throws Exception {
        String coordToken = authenticateAndGetToken("coord6", "COORDINADOR");
        long coordId = userIdFor("coord6");
        long pec = registerUser("pecNF", "PEC");
        long sup = registerUser("supNF", "ESPECIALISTA");
        String payload = visitUpdateJson(coordId, pec, sup, "Aula X", "2026-05-02", "09:30", "10:30", -10.5, -75.4, null);

        mockMvc.perform(put("/visitas/{id}", 99999)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + coordToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Visita no encontrada"));
    }

    @Test
    void registerEntryWithoutEvidenceSuccessfully() throws Exception {
        String coordToken = authenticateAndGetToken("coord7", "COORDINADOR");
        long coordId = userIdFor("coord7");
        long promoterId = registerUser("pecEntry", "PEC");
        long supervisorId = registerUser("supEntry", "ESPECIALISTA");
        long id = createVisit(coordToken, coordId, promoterId, supervisorId, "Aula 500", "2026-05-03", "09:00", "11:00");

        String pecToken = loginToken("pecEntry", "PEC");

        String payload = """
                {
                  "fechaHoraEntrada": "2026-05-03T09:05:00",
                  "latitud": -10.5809137,
                  "longitud": -75.4011529,
                  "observacion": "Ingreso sin novedad",
                  "evidenciaBase64": null
                }
                """;

        mockMvc.perform(post("/visitas/{id}/registrar-entrada", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + pecToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promoterArrivalConfirmedAt").isNotEmpty())
                .andExpect(jsonPath("$.promoterArrivalLatitude").value(-10.5809137))
                .andExpect(jsonPath("$.promoterArrivalLongitude").value(-75.4011529))
                .andExpect(jsonPath("$.status").value("EnCurso"));
    }

    @Test
    void registerEntryWithEvidenceSuccessfully() throws Exception {
        String coordToken = authenticateAndGetToken("coord8", "COORDINADOR");
        long coordId = userIdFor("coord8");
        long promoterId = registerUser("pecEv", "PEC");
        long supervisorId = registerUser("supEv", "ESPECIALISTA");
        long id = createVisit(coordToken, coordId, promoterId, supervisorId, "Aula 600", "2026-05-03", "10:00", "12:00");
        String pecToken = loginToken("pecEv", "PEC");
        String base64 = "aGVsbG8gd29ybGQ=";

        String payload = """
                {
                  "fechaHoraEntrada": "2026-05-03T10:02:00",
                  "latitud": -10.5809137,
                  "longitud": -75.4011529,
                  "observacion": "Moto estacionada en puerta principal",
                  "evidenciaBase64": "%s"
                }
                """.formatted(base64);

        mockMvc.perform(post("/visitas/{id}/registrar-entrada", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + pecToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EnCurso"));
    }

    @Test
    void promoterArrivalPatchReturnsTimestamp() throws Exception {
        String coordToken = authenticateAndGetToken("coord9", "COORDINADOR");
        long coordId = userIdFor("coord9");
        long promoterId = registerUser("pecPatch", "PEC");
        long supervisorId = registerUser("supPatch", "ESPECIALISTA");
        long id = createVisit(coordToken, coordId, promoterId, supervisorId, "Lugar X", "2026-05-04", "08:00", "09:00");
        String pecToken = loginToken("pecPatch", "PEC");

        String body = """
                {"latitude": -10.1, "longitude": -75.1, "observation": "ok", "evidenciaBase64": null}
                """;
        mockMvc.perform(patch("/visitas/{id}/promoter-arrival", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + pecToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visitId").value(String.valueOf(id)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void validationEndpointWhenIncomplete() throws Exception {
        String coordToken = authenticateAndGetToken("coord10", "COORDINADOR");
        long coordId = userIdFor("coord10");
        long promoterId = registerUser("pecVal", "PEC");
        long supervisorId = registerUser("supVal", "ESPECIALISTA");
        long id = createVisit(coordToken, coordId, promoterId, supervisorId, "Z", "2026-05-05", "08:00", "09:00");

        mockMvc.perform(get("/visitas/{id}/validation", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + coordToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isConsistent").value(false))
                .andExpect(jsonPath("$.status").value("INCOMPLETE"));
    }

    @Test
    void pecCannotCreateVisit() throws Exception {
        registerUser("pecOnly", "PEC");
        String pecToken = loginToken("pecOnly", "PEC");
        long coordId = registerUser("coordX", "COORDINADOR");
        long promoterId = registerUser("pecY", "PEC");
        long supId = registerUser("supZ", "ESPECIALISTA");
        String payload = visitCreateJson(coordId, promoterId, supId, "A", "2026-05-06", "08:00", "09:00", -10.0, -75.0);

        mockMvc.perform(post("/visitas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + pecToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    private long createVisit(
            String coordToken,
            long coordinatorId,
            long promoterId,
            long supervisorId,
            String place,
            String date,
            String start,
            String end
    ) throws Exception {
        String payload = visitCreateJson(coordinatorId, promoterId, supervisorId, place, date, start, end, -10.5, -75.4);
        MvcResult result = mockMvc.perform(post("/visitas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + coordToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return Long.parseLong(body.get("id").asText());
    }

    private String visitCreateJson(long coordinatorId, long promoterId, long supervisorId, String place, String date, String start, String end, double lat, double lon) {
        return """
                {
                  "coordinatorId": "%d",
                  "promoterId": "%d",
                  "supervisorId": "%d",
                  "placeName": "%s",
                  "scheduledDate": "%s",
                  "expectedStartTime": "%s",
                  "expectedEndTime": "%s",
                  "latitude": %s,
                  "longitude": %s
                }
                """.formatted(coordinatorId, promoterId, supervisorId, place, date, start, end, lat, lon);
    }

    private String visitUpdateJson(long coordinatorId, long promoterId, long supervisorId, String place, String date, String start, String end, double lat, double lon, String status) {
        String statusField = status == null ? "null" : "\"" + status + "\"";
        return """
                {
                  "coordinatorId": "%d",
                  "promoterId": "%d",
                  "supervisorId": "%d",
                  "placeName": "%s",
                  "scheduledDate": "%s",
                  "expectedStartTime": "%s",
                  "expectedEndTime": "%s",
                  "latitude": %s,
                  "longitude": %s,
                  "status": %s
                }
                """.formatted(coordinatorId, promoterId, supervisorId, place, date, start, end, lat, lon, statusField);
    }

    private String authenticateAndGetToken(String usuario, String perfil) throws Exception {
        String registerPayload = """
                {
                  "nombres": "Coord",
                  "apellidos": "Demo",
                  "usuario": "%s",
                  "password": "secret123",
                  "perfil": "%s"
                }
                """.formatted(usuario, perfil);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerPayload))
                .andExpect(status().isCreated());

        return loginToken(usuario, perfil);
    }

    private String loginToken(String usuario, String perfil) throws Exception {
        String loginPayload = """
                {
                  "usuario": "%s",
                  "password": "secret123",
                  "perfil": "%s"
                }
                """.formatted(usuario, perfil);
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginBody = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return loginBody.get("token").asText();
    }

    private long registerUser(String usuario, String perfil) throws Exception {
        String registerPayload = """
                {
                  "nombres": "User",
                  "apellidos": "Test",
                  "usuario": "%s",
                  "password": "secret123",
                  "perfil": "%s"
                }
                """.formatted(usuario, perfil);

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerPayload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }

    private long userIdFor(String usuario) throws Exception {
        String loginPayload = """
                {
                  "usuario": "%s",
                  "password": "secret123",
                  "perfil": "COORDINADOR"
                }
                """.formatted(usuario);
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode loginBody = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return loginBody.get("id").asLong();
    }
}
