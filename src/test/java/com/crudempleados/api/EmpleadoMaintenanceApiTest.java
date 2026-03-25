package com.crudempleados.api;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.model.Empleado;
import com.crudempleados.support.AbstractApiIntegrationTest;
import com.crudempleados.support.EmpleadoTestDataFactory;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EmpleadoMaintenanceApiTest extends AbstractApiIntegrationTest {

    @Test
    void shouldUpdateAndDeleteWithAuthentication() throws Exception {
        Empleado empleado = seedEmpleado("Nombre M0", "Direccion M0", "Telefono M0");
        String clave = "EMP-" + empleado.getClaveNumero();

        mockMvc.perform(put("/api/v1/empleados/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(EmpleadoTestDataFactory.updateRequest("M0"))))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/empleados/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldRequireAuthenticationForUpdateAndDelete() throws Exception {
        Empleado empleado = seedEmpleado("Nombre M1", "Direccion M1", "Telefono M1");
        String clave = "EMP-" + empleado.getClaveNumero();

        mockMvc.perform(put("/api/v1/empleados/{clave}", clave)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(EmpleadoTestDataFactory.updateRequest("M1"))))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/v1/empleados/{clave}", clave))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectInvalidUpdatePayloadAndExposeFieldRule() throws Exception {
        Empleado empleado = seedEmpleado("Nombre M2", "Direccion M2", "Telefono M2");
        String clave = "EMP-" + empleado.getClaveNumero();

        Map<String, Object> invalidPayload = EmpleadoTestDataFactory.updateRequest("M2");
        invalidPayload.put("nombre", "x".repeat(101));

        mockMvc.perform(put("/api/v1/empleados/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPayload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("nombre")))
            .andExpect(jsonPath("$.message", containsString("100")));
    }

    @Test
    void shouldUpdateDepartamentoAssignmentWhenDepartamentoClaveIsProvided() throws Exception {
        Empleado empleado = seedEmpleado("Nombre M3", "Direccion M3", "Telefono M3");
        String clave = "EMP-" + empleado.getClaveNumero();
        String departamentoClave = seedDepartamentoClave("Dept Maintenance Reassign");

        mockMvc.perform(put("/api/v1/empleados/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(EmpleadoTestDataFactory.updateRequest("M3", departamentoClave))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.departamentoClave").value(departamentoClave));
    }

    @Test
    void shouldReturn404WhenUpdateUsesUnknownDepartamentoClave() throws Exception {
        Empleado empleado = seedEmpleado("Nombre M4", "Direccion M4", "Telefono M4");
        String clave = "EMP-" + empleado.getClaveNumero();
        Map<String, Object> payload = EmpleadoTestDataFactory.updateRequest("M4", "DEP-99999");

        mockMvc.perform(put("/api/v1/empleados/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", containsString("Departamento not found")));
    }
}
