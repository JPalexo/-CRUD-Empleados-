package com.crudempleados.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import com.crudempleados.support.AbstractApiIntegrationTest;
import com.crudempleados.support.EmpleadoTestDataFactory;
import org.junit.jupiter.api.Test;

class EmpleadoCreateLoginSlaIT extends AbstractApiIntegrationTest {

    @Test
    void shouldCompleteCreateToFirstLoginWithinSixtySeconds() throws Exception {
        String email = "empleado.sla@empresa.com";
        String password = "abc12345";
        String departamentoClave = seedDepartamentoClave("Dept SLA");
        Map<String, Object> createPayload = EmpleadoTestDataFactory.createRequestWithCredentials("SLA1", email, password, departamentoClave);

        Instant start = Instant.now();

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/empleados/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    EmpleadoTestDataFactory.loginRequest(email, password))))
            .andExpect(status().isOk());

        Duration elapsed = Duration.between(start, Instant.now());
        assertThat(elapsed).isLessThanOrEqualTo(Duration.ofSeconds(60));
    }
}