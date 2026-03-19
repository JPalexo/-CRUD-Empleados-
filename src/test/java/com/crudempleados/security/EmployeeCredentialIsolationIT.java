package com.crudempleados.security;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import com.crudempleados.support.AbstractApiIntegrationTest;
import com.crudempleados.support.EmpleadoTestDataFactory;
import org.junit.jupiter.api.Test;

class EmployeeCredentialIsolationIT extends AbstractApiIntegrationTest {

    @Test
    void shouldRejectAdminCrudAccessWithEmployeeCredentials() throws Exception {
        String employeeEmail = "empleado.isolation@empresa.com";
        String employeePassword = "abc12345";
        Map<String, Object> createPayload = EmpleadoTestDataFactory.createRequestWithCredentials(
            "ISO1", employeeEmail, employeePassword);

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue(employeeEmail, employeePassword)))
            .andExpect(status().isUnauthorized());
    }
}