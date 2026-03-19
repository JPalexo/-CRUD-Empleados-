package com.crudempleados.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.support.AbstractApiIntegrationTest;
import com.crudempleados.support.EmpleadoTestDataFactory;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class EmpleadoCrudWorkflowTimedIT extends AbstractApiIntegrationTest {

    @Test
    void shouldCompleteCrudWorkflowInUnderThreeMinutes() throws Exception {
        Instant start = Instant.now();

        MvcResult createResult = mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(EmpleadoTestDataFactory.createRequest("WF"))))
            .andExpect(status().isCreated())
            .andReturn();

        JsonNode createJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String clave = createJson.get("clave").asText();

        mockMvc.perform(get("/api/v1/empleados/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk());

        Map<String, Object> updatePayload = EmpleadoTestDataFactory.updateRequest("WF");
        mockMvc.perform(put("/api/v1/empleados/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePayload)))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/empleados/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNoContent());

        Duration elapsed = Duration.between(start, Instant.now());
        assertThat(elapsed).isLessThan(Duration.ofMinutes(3));
    }
}
