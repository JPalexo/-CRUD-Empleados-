package com.crudempleados.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.support.AbstractApiIntegrationTest;
import com.crudempleados.support.EmpleadoTestDataFactory;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EmpleadoCreateApiTest extends AbstractApiIntegrationTest {

    @Test
    void shouldRequireAuthenticationForCreate() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(EmpleadoTestDataFactory.createRequest("A"))))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateEmpleadoAndReturnCanonicalClave() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(EmpleadoTestDataFactory.createRequest("B"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clave", matchesPattern("EMP-[1-9][0-9]*")))
            .andExpect(jsonPath("$.nombre").value("Nombre B"));
    }

    @Test
    void shouldRejectDuplicateEmailUsingNormalizedValue() throws Exception {
        Map<String, Object> firstPayload = EmpleadoTestDataFactory.createRequestWithCredentials(
            "D1", "  Empleado.Dup@Empresa.com  ", "abc12345");
        Map<String, Object> secondPayload = EmpleadoTestDataFactory.createRequestWithCredentials(
            "D2", "empleado.dup@empresa.com", "abc12345");

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstPayload)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondPayload)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message", containsString("email")));
    }

    @Test
    void shouldRejectManualClaveInCreatePayload() throws Exception {
        String payload = """
            {
              \"clave\": \"EMP-999\",
              \"nombre\": \"Nombre Manual\",
              \"direccion\": \"Direccion Manual\",
              \"telefono\": \"Telefono Manual\"
            }
            """;

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("clave")));
    }
}
