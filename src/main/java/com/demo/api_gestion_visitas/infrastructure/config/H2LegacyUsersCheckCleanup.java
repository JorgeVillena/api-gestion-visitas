package com.demo.api_gestion_visitas.infrastructure.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Bases H2 creadas con un enum antiguo (p. ej. ADMIN/COORDINADOR/DIRECTOR) dejan un CHECK en {@code users.perfil}
 * que Hibernate {@code ddl-auto=update} no elimina. Eso rechaza valores actuales del enum ({@code PEC}, {@code ESPECIALISTA}, etc.).
 */
@Configuration
public class H2LegacyUsersCheckCleanup {

    @Bean
    @Order(0)
    ApplicationRunner dropObsoleteUsersPerfilCheckConstraints(
            DataSource dataSource,
            @Value("${spring.datasource.url:}") String jdbcUrl) {
        return args -> {
            if (jdbcUrl == null || !jdbcUrl.contains(":h2:")) {
                return;
            }
            try (Connection c = dataSource.getConnection(); Statement st = c.createStatement()) {
                List<String[]> constraintTable = new ArrayList<>();
                try (ResultSet rs = st.executeQuery("""
                        SELECT CONSTRAINT_NAME, TABLE_NAME
                        FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
                        WHERE TABLE_SCHEMA = 'PUBLIC'
                          AND UPPER(TABLE_NAME) = 'USERS'
                          AND CONSTRAINT_TYPE = 'CHECK'
                        """)) {
                    while (rs.next()) {
                        constraintTable.add(new String[] { rs.getString(1), rs.getString(2) });
                    }
                }
                for (String[] row : constraintTable) {
                    String cName = row[0];
                    String tName = row[1];
                    st.execute("ALTER TABLE " + quote(tName) + " DROP CONSTRAINT IF EXISTS " + quote(cName));
                }
            }
        };
    }

    private static String quote(String id) {
        return "\"" + id.replace("\"", "\"\"") + "\"";
    }
}
