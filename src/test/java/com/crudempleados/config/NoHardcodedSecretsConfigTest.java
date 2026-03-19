package com.crudempleados.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class NoHardcodedSecretsConfigTest {

    @Test
    void shouldUseExternalizedSecretsInApplicationConfig() throws IOException {
        String applicationYml = Files.readString(Path.of("src/main/resources/application.yml"));

        assertThat(applicationYml).contains("${CRUD_DB_PASSWORD}");
        assertThat(applicationYml).contains("${CRUD_BASIC_PASSWORD}");
        assertThat(applicationYml).doesNotContain("admin123");
        assertThat(applicationYml).doesNotContain("empleados_pass");
    }

    @Test
    void shouldAvoidHardcodedCredentialLiteralsInComposeFile() throws IOException {
        String compose = Files.readString(Path.of("docker-compose.yml"));

        assertThat(compose).contains("${CRUD_DB_PASSWORD}");
        assertThat(compose).contains("${CRUD_BASIC_PASSWORD}");
        assertThat(compose).doesNotContain("POSTGRES_PASSWORD: empleados_pass");
    }
}
