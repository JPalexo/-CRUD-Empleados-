package com.crudempleados.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.model.Departamento;
import com.crudempleados.model.Empleado;
import com.crudempleados.support.AbstractApiIntegrationTest;
import org.junit.jupiter.api.Test;

class DepartamentoMaintenanceApiTest extends AbstractApiIntegrationTest {

    @Test
    void shouldCreateUpdateWithNormalizationAndRejectDuplicateByNormalizedName() throws Exception {
        mockMvc.perform(post("/api/v1/departamentos")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content("{\"nombre\":\"  Ventas  \"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clave", matchesPattern("DEP-[1-9][0-9]*")))
            .andExpect(jsonPath("$.nombre").value("Ventas"));

        mockMvc.perform(post("/api/v1/departamentos")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content("{\"nombre\":\"ventas\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message", containsString("already exists")));

        Departamento departamento = seedDepartamento("Operaciones");
        String clave = "DEP-" + departamento.getClaveNumero();

        mockMvc.perform(put("/api/v1/departamentos/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content("{\"nombre\":\"  Comercial  \"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Comercial"));
    }

    @Test
    void shouldDeleteEmptyDepartamentoWith204() throws Exception {
        Departamento departamento = seedDepartamento("Temporal");
        String clave = "DEP-" + departamento.getClaveNumero();

        mockMvc.perform(delete("/api/v1/departamentos/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/departamentos/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenDeletingDepartamentoInUse() throws Exception {
        Departamento departamento = seedDepartamento("Soporte");
        Empleado empleado = seedEmpleado("Nombre SO", "Direccion SO", "Telefono SO");
        assignEmpleadoToDepartamento(empleado, departamento);
        String clave = "DEP-" + departamento.getClaveNumero();

        mockMvc.perform(delete("/api/v1/departamentos/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message", containsString("cannot be deleted")));
    }
}