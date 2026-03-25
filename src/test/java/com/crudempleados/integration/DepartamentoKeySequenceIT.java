package com.crudempleados.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.model.Departamento;
import com.crudempleados.support.AbstractApiIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class DepartamentoKeySequenceIT extends AbstractApiIntegrationTest {

    @Test
    void shouldNotReuseDepartamentoSequenceAfterDeletion() throws Exception {
        MvcResult createFirst = mockMvc.perform(post("/api/v1/departamentos")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content("{\"nombre\":\"Secuencia Uno\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clave", matchesPattern("DEP-[1-9][0-9]*")))
            .andReturn();

        String clave1 = objectMapper.readTree(createFirst.getResponse().getContentAsString()).get("clave").asText();
        long numero1 = Long.parseLong(clave1.substring("DEP-".length()));

        Departamento temporal = seedDepartamento("Temporal Empty");
        String claveTemporal = "DEP-" + temporal.getClaveNumero();
        mockMvc.perform(delete("/api/v1/departamentos/{clave}", claveTemporal)
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNoContent());

        MvcResult createSecond = mockMvc.perform(post("/api/v1/departamentos")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content("{\"nombre\":\"Secuencia Dos\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clave", matchesPattern("DEP-[1-9][0-9]*")))
            .andReturn();

        String clave2 = objectMapper.readTree(createSecond.getResponse().getContentAsString()).get("clave").asText();
        long numero2 = Long.parseLong(clave2.substring("DEP-".length()));

        assertThat(numero2).isGreaterThan(numero1);
    }
}
