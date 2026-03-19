package com.crudempleados.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import com.crudempleados.support.AbstractApiIntegrationTest;
import com.crudempleados.support.EmpleadoTestDataFactory;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class EmpleadoLoginApiTest extends AbstractApiIntegrationTest {

    @Test
    void shouldAllowPublicLoginSuccessAndReturnIdentityWithoutToken() throws Exception {
        createEmpleadoConCredenciales("LGN1", "empleado.login1@empresa.com", "abc12345");

        Map<String, Object> loginPayload = EmpleadoTestDataFactory.loginRequest(
            " EMPLEADO.LOGIN1@EMPRESA.COM ", "abc12345");

        mockMvc.perform(post("/api/v1/empleados/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginPayload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.authenticated").value(true))
            .andExpect(jsonPath("$.empleado.clave", matchesPattern("EMP-[1-9][0-9]*")))
            .andExpect(jsonPath("$.empleado.email").value("empleado.login1@empresa.com"))
            .andExpect(jsonPath("$.token").doesNotExist())
            .andExpect(jsonPath("$.sessionId").doesNotExist());
    }

    @Test
    void shouldReturnGenericUnauthorizedForInvalidCredentialsAndUnknownEmail() throws Exception {
        createEmpleadoConCredenciales("LGN2", "empleado.login2@empresa.com", "abc12345");

        Map<String, Object> wrongPassword = EmpleadoTestDataFactory.loginRequest(
            "empleado.login2@empresa.com", "wrong123");
        MvcResult wrongPasswordResult = mockMvc.perform(post("/api/v1/empleados/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongPassword)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message", containsString("Employee authentication failed")))
            .andReturn();

        Map<String, Object> unknownEmail = EmpleadoTestDataFactory.loginRequest(
            "desconocido@empresa.com", "abc12345");
        MvcResult unknownEmailResult = mockMvc.perform(post("/api/v1/empleados/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unknownEmail)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message", containsString("Employee authentication failed")))
            .andReturn();

        JsonNode wrongJson = objectMapper.readTree(wrongPasswordResult.getResponse().getContentAsString());
        JsonNode unknownJson = objectMapper.readTree(unknownEmailResult.getResponse().getContentAsString());
        org.assertj.core.api.Assertions.assertThat(wrongJson.get("message").asText())
            .isEqualTo(unknownJson.get("message").asText());
    }

    private void createEmpleadoConCredenciales(String seed, String email, String password) throws Exception {
        Map<String, Object> payload = EmpleadoTestDataFactory.createRequestWithCredentials(seed, email, password);
        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated());
    }
}