package com.crudempleados.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.crudempleados.CrudEmpleadosApplication;
import com.crudempleados.support.AbstractApiIntegrationTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

class ExternalizedSecretsConfigIT extends AbstractApiIntegrationTest {

    @Autowired
    private Environment environment;

    @Test
    void shouldStartWithProfileBasedExternalizedCredentials() {
        assertThat(environment.getProperty("CRUD_DB_HOST")).isNotBlank();
        assertThat(environment.getProperty("CRUD_DB_PORT")).isNotBlank();
        assertThat(environment.getProperty("CRUD_DB_NAME")).isNotBlank();
        assertThat(environment.getProperty("CRUD_DB_USER")).isNotBlank();
        assertThat(environment.getProperty("CRUD_DB_PASSWORD")).isNotBlank();
    }

    @Test
    void shouldFailFastWhenRequiredDbSecretsAreMissing() {
        SpringApplication app = new SpringApplication(CrudEmpleadosApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);

        Throwable thrown = catchThrowable(() -> {
            try (ConfigurableApplicationContext ignored = app.run(
                "--CRUD_BASIC_USER=admin",
                "--CRUD_BASIC_PASSWORD=admin123",
                "--CRUD_DB_HOST=",
                "--CRUD_DB_PORT=",
                "--CRUD_DB_NAME=",
                "--CRUD_DB_USER=",
                "--CRUD_DB_PASSWORD="
            )) {
                // This context should not start successfully.
            }
        });

        assertThat(thrown).isNotNull();
        assertThat(rootCauseMessage(thrown)).contains("Missing required datasource secret(s)");
    }

    private String rootCauseMessage(Throwable throwable) {
        Throwable current = throwable;
        List<String> messages = new ArrayList<>();
        while (current != null) {
            if (current.getMessage() != null) {
                messages.add(current.getMessage());
            }
            current = current.getCause();
        }
        return String.join(" | ", messages);
    }
}
