package com.demo.api_gestion_visitas.infrastructure.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Bases H2 antiguas pueden dejar {@code users.perfil} como ENUM/constraint obsoleto
 * (p. ej. ADMIN/COORDINADOR/DIRECTOR). Esto bloquea perfiles actuales como PEC/ESPECIALISTA.
 */
@Configuration
@Profile("local")
public class H2LegacyUsersCheckCleanup {

    @Bean
    @Order(0)
    ApplicationRunner migrateLegacyUsersPerfilConstraints(
            DataSource dataSource,
            @Value("${spring.datasource.url:}") String jdbcUrl) {
        return args -> {
            if (jdbcUrl == null || !jdbcUrl.contains(":h2:")) {
                return;
            }
            try (Connection c = dataSource.getConnection(); Statement st = c.createStatement()) {
                // 1) Si PERFIL quedó como ENUM, lo pasamos a VARCHAR para remover restricción implícita.
                if (tableExists(st, "USERS")) {
                    st.execute("ALTER TABLE \"USERS\" ALTER COLUMN \"PERFIL\" VARCHAR(64)");
                }

                // 2) Limpia CHECKs heredados que Hibernate no siempre remueve en ddl-auto=update.
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

    private static boolean tableExists(Statement st, String tableNameUpper) throws java.sql.SQLException {
        String sql = "SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' AND UPPER(TABLE_NAME) = '" + tableNameUpper + "'";
        try (ResultSet rs = st.executeQuery(sql)) {
            return rs.next();
        }
    }

    private static String quote(String id) {
        return "\"" + id.replace("\"", "\"\"") + "\"";
    }
}
