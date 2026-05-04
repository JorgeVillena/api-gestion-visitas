package com.demo.api_gestion_visitas.domain.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileJacksonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesCanonicalNames() throws Exception {
        assertThat(mapper.writeValueAsString(Profile.PEC)).isEqualTo("\"PEC\"");
        assertThat(mapper.writeValueAsString(Profile.ESPECIALISTA)).isEqualTo("\"ESPECIALISTA\"");
    }

    @Test
    void deserializesPromotorAndSupervisorAliases() throws Exception {
        assertThat(mapper.readValue("\"PROMOTOR\"", Profile.class)).isEqualTo(Profile.PEC);
        assertThat(mapper.readValue("\"promotor\"", Profile.class)).isEqualTo(Profile.PEC);
        assertThat(mapper.readValue("\"SUPERVISOR\"", Profile.class)).isEqualTo(Profile.ESPECIALISTA);
        assertThat(mapper.readValue("\"ADMIN\"", Profile.class)).isEqualTo(Profile.ESPECIALISTA);
    }

    @Test
    void rejectsProfesor() {
        assertThatThrownBy(() -> mapper.readValue("\"PROFESOR\"", Profile.class))
                .hasRootCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void securityRoleMapsLegacyAdminClaim() {
        assertThat(Profile.toSecurityRoleName("ADMIN")).isEqualTo("ESPECIALISTA");
        assertThat(Profile.toSecurityRoleName("ESPECIALISTA")).isEqualTo("ESPECIALISTA");
        assertThat(Profile.toSecurityRoleName("PEC")).isEqualTo("PEC");
    }
}
