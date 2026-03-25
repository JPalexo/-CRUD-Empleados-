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
        String departamentoClave = seedDepartamentoClave("Dept Auth");
        mockMvc.perform(post("/api/v1/empleados")
                .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(EmpleadoTestDataFactory.createRequest("A", departamentoClave))))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateEmpleadoAndReturnCanonicalClave() throws Exception {
        String departamentoClave = seedDepartamentoClave("Dept Create");
        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(EmpleadoTestDataFactory.createRequest("B", departamentoClave))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clave", matchesPattern("EMP-[1-9][0-9]*")))
            .andExpect(jsonPath("$.nombre").value("Nombre B"))
            .andExpect(jsonPath("$.departamentoClave").value(departamentoClave));
    }

    @Test
    void shouldRejectDuplicateEmailUsingNormalizedValue() throws Exception {
        String departamentoClave = seedDepartamentoClave("Dept Dup");
        Map<String, Object> firstPayload = EmpleadoTestDataFactory.createRequestWithCredentials(
            "D1", "  Empleado.Dup@Empresa.com  ", "abc12345", departamentoClave);
        Map<String, Object> secondPayload = EmpleadoTestDataFactory.createRequestWithCredentials(
            "D2", "empleado.dup@empresa.com", "abc12345", departamentoClave);

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
                String departamentoClave = seedDepartamentoClave("Dept Manual");
        String payload = """
            {
              \"clave\": \"EMP-999\",
              \"nombre\": \"Nombre Manual\",
              \"direccion\": \"Direccion Manual\",
                            \"telefono\": \"Telefono Manual\",
                            \"email\": \"manual@empresa.com\",
                            \"password\": \"abc12345\",
                            \"departamentoClave\": \"%s\"
            }
                        """.formatted(departamentoClave);

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("clave")));
    }

    @Test
    void shouldRejectCreateWhenDepartamentoClaveIsMissing() throws Exception {
        String payload = """
            {
              \"nombre\": \"Nombre Sin Departamento\",
              \"direccion\": \"Direccion\",
              \"telefono\": \"Telefono\",
              \"email\": \"sin.departamento@empresa.com\",
              \"password\": \"abc12345\"
            }
            """;

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("departamentoClave")));
    }

    @Test
    void shouldReturn404WhenDepartamentoClaveDoesNotExist() throws Exception {
        String payload = """
            {
              \"nombre\": \"Nombre Sin Departamento Existente\",
              \"direccion\": \"Direccion\",
              \"telefono\": \"Telefono\",
              \"email\": \"departamento.inexistente@empresa.com\",
              \"password\": \"abc12345\",
              \"departamentoClave\": \"DEP-99999\"
            }
            """;

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", containsString("Departamento not found")));
    }
}
