package com.crudempleados.integration;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.model.Empleado;
import com.crudempleados.support.AbstractApiIntegrationTest;
import com.crudempleados.support.EmpleadoTestDataFactory;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EmpleadoMaintenanceIT extends AbstractApiIntegrationTest {

    @Test
    void shouldPersistUpdatedMutableFields() throws Exception {
        Empleado empleado = seedEmpleado("Nombre U1", "Direccion U1", "Telefono U1");
        String clave = "EMP-" + empleado.getClaveNumero();
        Map<String, Object> updatePayload = EmpleadoTestDataFactory.updateRequest("U1");

        mockMvc.perform(put("/api/v1/empleados/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePayload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Actualizado U1"));

        mockMvc.perform(get("/api/v1/empleados/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.direccion").value("Nueva direccion U1"));
    }

    @Test
    void shouldDeleteEmployeeAndReturnNotFoundAfterward() throws Exception {
        Empleado empleado = seedEmpleado("Nombre D1", "Direccion D1", "Telefono D1");
        String clave = "EMP-" + empleado.getClaveNumero();

        mockMvc.perform(delete("/api/v1/empleados/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/empleados/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForUpdateAndDeleteOnUnknownClave() throws Exception {
        Map<String, Object> updatePayload = EmpleadoTestDataFactory.updateRequest("NF");

        mockMvc.perform(put("/api/v1/empleados/{clave}", "EMP-99999")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePayload)))
            .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/v1/empleados/{clave}", "EMP-99999")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNotFound());
    }
}
