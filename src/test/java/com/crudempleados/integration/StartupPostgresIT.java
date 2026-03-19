package com.crudempleados.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
class StartupPostgresIT {

    @Container
    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("empleados")
        .withUsername("empleados_app")
        .withPassword("empleados_pass");

    @Test
    void shouldStartPostgresAndAllowConnections() throws Exception {
        assertThat(POSTGRES.isRunning()).isTrue();
        try (Connection connection = DriverManager.getConnection(
            POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
            assertThat(connection.isValid(2)).isTrue();
        }
    }

    @Test
    void shouldProvideClearFailureWhenTargetIsUnavailable() {
        SQLException error = assertThrows(SQLException.class,
            () -> DriverManager.getConnection("jdbc:postgresql://127.0.0.1:1/empleados", "x", "y"));
        assertThat(error.getMessage().toLowerCase()).contains("connection");
    }
}
