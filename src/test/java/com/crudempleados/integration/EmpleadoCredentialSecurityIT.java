package com.crudempleados.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import com.crudempleados.model.EmpleadoCredencial;
import com.crudempleados.support.AbstractApiIntegrationTest;
import com.crudempleados.support.EmpleadoTestDataFactory;
import org.junit.jupiter.api.Test;

class EmpleadoCredentialSecurityIT extends AbstractApiIntegrationTest {

    @Test
    void shouldPersistOnlyHashAndNeverPlaintextPassword() throws Exception {
        String rawPassword = "abc12345";
        String email = "empleado.security@empresa.com";
        String departamentoClave = seedDepartamentoClave("Dept SEC1");
        Map<String, Object> payload = EmpleadoTestDataFactory.createRequestWithCredentials("SEC1", email, rawPassword, departamentoClave);

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated());

        EmpleadoCredencial credencial = empleadoCredencialRepository.findByEmailNormalizado(email).orElseThrow();

        assertThat(credencial.getPasswordHash()).isNotBlank();
        assertThat(credencial.getPasswordHash()).isNotEqualTo(rawPassword);
        assertThat(credencial.getPasswordHash()).startsWith("{bcrypt}");
        assertThat(credencial.getPasswordHash()).doesNotContain("abc12345");
    }
}