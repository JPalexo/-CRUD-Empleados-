package com.crudempleados.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.support.AbstractApiIntegrationTest;
import com.crudempleados.support.EmpleadoTestDataFactory;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class EmpleadoCreateSequenceIT extends AbstractApiIntegrationTest {

    @Test
    void shouldGenerateConsecutiveEmployeeKeys() throws Exception {
        long first = createAndExtractKey("S1");
        long second = createAndExtractKey("S2");

        assertThat(second).isEqualTo(first + 1);
    }

    @Test
    void shouldKeepSequenceMonotonicAfterDataDeletion() throws Exception {
        long first = createAndExtractKey("R1");
        empleadoRepository.deleteAll();
        long second = createAndExtractKey("R2");

        assertThat(second).isGreaterThan(first);
    }

    private long createAndExtractKey(String seed) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(EmpleadoTestDataFactory.createRequest(seed))))
            .andExpect(status().isCreated())
            .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return parseNumeroFromClave(json.get("clave").asText());
    }
}
