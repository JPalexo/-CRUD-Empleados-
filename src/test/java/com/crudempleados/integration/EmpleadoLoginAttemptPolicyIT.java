package com.crudempleados.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import com.crudempleados.model.EmpleadoLoginEvento;
import com.crudempleados.service.EmpleadoLoginEventService;
import com.crudempleados.support.AbstractApiIntegrationTest;
import com.crudempleados.support.EmpleadoTestDataFactory;
import org.junit.jupiter.api.Test;

class EmpleadoLoginAttemptPolicyIT extends AbstractApiIntegrationTest {

    @Test
    void shouldKeepAccountUnlockedAfterFiveFailuresAndLogEachAttempt() throws Exception {
        String email = "empleado.policy@empresa.com";
        Map<String, Object> createPayload = EmpleadoTestDataFactory.createRequestWithCredentials(
            "POL1", email, "abc12345");

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload)))
            .andExpect(status().isCreated());

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/v1/empleados/login")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        EmpleadoTestDataFactory.loginRequest(email, "wrong123"))))
                .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/v1/empleados/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    EmpleadoTestDataFactory.loginRequest(email, "abc12345"))))
            .andExpect(status().isOk());

        List<EmpleadoLoginEvento> eventos = empleadoLoginEventoRepository.findAllByEmailNormalizadoInputOrderByIdAsc(email);
        long failCount = eventos.stream()
            .filter(e -> EmpleadoLoginEventService.RESULT_FAILURE.equals(e.getResultado()))
            .count();
        long successCount = eventos.stream()
            .filter(e -> EmpleadoLoginEventService.RESULT_SUCCESS.equals(e.getResultado()))
            .count();

        assertThat(eventos).hasSize(6);
        assertThat(failCount).isEqualTo(5);
        assertThat(successCount).isEqualTo(1);
    }
}