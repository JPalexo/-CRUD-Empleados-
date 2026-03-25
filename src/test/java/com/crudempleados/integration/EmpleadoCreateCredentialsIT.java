package com.crudempleados.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import com.crudempleados.model.EmpleadoCredencial;
import com.crudempleados.support.AbstractApiIntegrationTest;
import com.crudempleados.support.EmpleadoTestDataFactory;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class EmpleadoCreateCredentialsIT extends AbstractApiIntegrationTest {

    @Test
    void shouldPersistCredentialWithNormalizedEmailAndHashedPassword() throws Exception {
        String departamentoClave = seedDepartamentoClave("Dept CRED1");
        Map<String, Object> payload = EmpleadoTestDataFactory.createRequestWithCredentials(
            "CRED1", "  EMPLEADO.CRED1@EMPRESA.COM ", "abc12345", departamentoClave);

        MvcResult result = mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        String clave = json.get("clave").asText();
        long claveNumero = parseNumeroFromClave(clave);

        EmpleadoCredencial credencial = empleadoCredencialRepository.findByEmailNormalizado("empleado.cred1@empresa.com")
            .orElseThrow();

        assertThat(credencial.getClaveNumero()).isEqualTo(claveNumero);
        assertThat(credencial.getHabilitada()).isTrue();
        assertThat(credencial.getPasswordHash()).isNotBlank();
        assertThat(credencial.getPasswordHash()).isNotEqualTo("abc12345");
        assertThat(credencial.getPasswordHash()).contains("{");
    }

    @Test
    void shouldRollbackEmployeeCreationWhenDuplicateEmailCausesCredentialConflict() throws Exception {
        String departamentoClave = seedDepartamentoClave("Dept CRED2");
        Map<String, Object> first = EmpleadoTestDataFactory.createRequestWithCredentials(
            "CRED2A", "dup.atomic@empresa.com", "abc12345", departamentoClave);
        Map<String, Object> duplicate = EmpleadoTestDataFactory.createRequestWithCredentials(
            "CRED2B", "  DUP.ATOMIC@EMPRESA.COM ", "abc12345", departamentoClave);

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(first)))
            .andExpect(status().isCreated());

        long empleadosBefore = empleadoRepository.count();
        long credencialesBefore = empleadoCredencialRepository.count();

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicate)))
            .andExpect(status().isConflict());

        assertThat(empleadoRepository.count()).isEqualTo(empleadosBefore);
        assertThat(empleadoCredencialRepository.count()).isEqualTo(credencialesBefore);
    }
}