package com.crudempleados.integration;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import com.crudempleados.model.EmpleadoCredencial;
import com.crudempleados.support.AbstractApiIntegrationTest;
import com.crudempleados.support.EmpleadoTestDataFactory;
import org.junit.jupiter.api.Test;

class EmpleadoLoginIT extends AbstractApiIntegrationTest {

    @Test
    void shouldAuthenticateUsingTrimAndLowercaseEmailNormalization() throws Exception {
        Map<String, Object> createPayload = EmpleadoTestDataFactory.createRequestWithCredentials(
            "NORM1", "  EMPLEADO.NORM1@EMPRESA.COM  ", "abc12345");

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload)))
            .andExpect(status().isCreated());

        Map<String, Object> loginPayload = EmpleadoTestDataFactory.loginRequest(
            " empleado.norm1@empresa.com ", "abc12345");

        mockMvc.perform(post("/api/v1/empleados/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginPayload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.authenticated").value(true))
            .andExpect(jsonPath("$.empleado.email").value("empleado.norm1@empresa.com"));
    }

    @Test
    void shouldRejectLoginForDeletedEmployeeAndNonEnabledCredential() throws Exception {
        Map<String, Object> createDeletedPayload = EmpleadoTestDataFactory.createRequestWithCredentials(
            "DEL1", "empleado.del1@empresa.com", "abc12345");
        String deletedClave = objectMapper.readTree(mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDeletedPayload)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString()).get("clave").asText();

        mockMvc.perform(delete("/api/v1/empleados/{clave}", deletedClave)
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/v1/empleados/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    EmpleadoTestDataFactory.loginRequest("empleado.del1@empresa.com", "abc12345"))))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message", containsString("Employee authentication failed")));

        Map<String, Object> createDisabledPayload = EmpleadoTestDataFactory.createRequestWithCredentials(
            "DIS1", "empleado.dis1@empresa.com", "abc12345");
        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDisabledPayload)))
            .andExpect(status().isCreated());

        EmpleadoCredencial credencial = empleadoCredencialRepository.findByEmailNormalizado("empleado.dis1@empresa.com")
            .orElseThrow();
        credencial.setHabilitada(false);
        empleadoCredencialRepository.save(credencial);

        mockMvc.perform(post("/api/v1/empleados/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    EmpleadoTestDataFactory.loginRequest("empleado.dis1@empresa.com", "abc12345"))))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message", containsString("Employee authentication failed")));
    }
}