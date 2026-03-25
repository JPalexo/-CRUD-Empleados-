package com.crudempleados.api;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.support.AbstractApiIntegrationTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EmpleadoCreateValidationTest extends AbstractApiIntegrationTest {

    @Test
    void shouldAcceptFieldsWithLengthExactly100() throws Exception {
        String length100 = "a".repeat(100);
        String departamentoClave = seedDepartamentoClave("Dept Validacion 100");
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombre", length100);
        payload.put("direccion", length100);
        payload.put("telefono", length100);
        payload.put("email", "validacion.100@empresa.com");
        payload.put("password", "abc12345");
        payload.put("departamentoClave", departamentoClave);

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldRejectFieldLengthAbove100AndExposeFieldRule() throws Exception {
        String length101 = "b".repeat(101);
        String departamentoClave = seedDepartamentoClave("Dept Validacion 101");
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombre", length101);
        payload.put("direccion", "Direccion");
        payload.put("telefono", "Telefono");
        payload.put("email", "validacion.101@empresa.com");
        payload.put("password", "abc12345");
        payload.put("departamentoClave", departamentoClave);

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("nombre")))
            .andExpect(jsonPath("$.message", containsString("100")));
    }

    @Test
    void shouldRejectInvalidEmailAndExposeFieldRule() throws Exception {
        String departamentoClave = seedDepartamentoClave("Dept Validacion Email");
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombre", "Nombre Email");
        payload.put("direccion", "Direccion Email");
        payload.put("telefono", "Telefono Email");
        payload.put("email", "not-an-email");
        payload.put("password", "abc12345");
        payload.put("departamentoClave", departamentoClave);

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("email")));
    }

    @Test
    void shouldRejectPasswordWithoutNumberAndExposeFieldRule() throws Exception {
        String departamentoClave = seedDepartamentoClave("Dept Validacion Password");
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombre", "Nombre Password");
        payload.put("direccion", "Direccion Password");
        payload.put("telefono", "Telefono Password");
        payload.put("email", "validacion.password@empresa.com");
        payload.put("password", "abcdefgh");
        payload.put("departamentoClave", departamentoClave);

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("password")));
    }
}
