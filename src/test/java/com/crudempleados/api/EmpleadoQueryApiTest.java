package com.crudempleados.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.model.Departamento;
import com.crudempleados.model.Empleado;
import com.crudempleados.support.AbstractApiIntegrationTest;
import org.junit.jupiter.api.Test;

class EmpleadoQueryApiTest extends AbstractApiIntegrationTest {

    @Test
    void shouldListAndFetchEmpleadoByClave() throws Exception {
        Departamento departamento = seedDepartamento("Dept Q1");
        String departamentoClave = "DEP-" + departamento.getClaveNumero();
        Empleado first = seedEmpleado("Nombre Q1", "Direccion Q1", "Telefono Q1");
        assignEmpleadoToDepartamento(first, departamento);
        seedEmpleado("Nombre Q2", "Direccion Q2", "Telefono Q2");
        String clave = "EMP-" + first.getClaveNumero();

        mockMvc.perform(get("/api/v1/empleados")
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.pagination.page").value(0))
            .andExpect(jsonPath("$.pagination.size").value(10))
            .andExpect(jsonPath("$.pagination.totalElements").value(2))
            .andExpect(jsonPath("$.pagination.totalPages").value(1));

        mockMvc.perform(get("/api/v1/empleados/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clave").value(clave))
            .andExpect(jsonPath("$.nombre").value("Nombre Q1"))
            .andExpect(jsonPath("$.departamentoClave").value(departamentoClave));
    }

    @Test
    void shouldApplyPaginationDefaultsWhenParametersAreOmitted() throws Exception {
        seedEmpleado("Nombre D1", "Direccion D1", "Telefono D1");
        seedEmpleado("Nombre D2", "Direccion D2", "Telefono D2");

        mockMvc.perform(get("/api/v1/empleados")
                .param("size", "1")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.pagination.page").value(0))
            .andExpect(jsonPath("$.pagination.size").value(1));

        mockMvc.perform(get("/api/v1/empleados")
                .param("page", "0")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pagination.page").value(0))
            .andExpect(jsonPath("$.pagination.size").value(10))
            .andExpect(jsonPath("$.pagination.totalElements").value(2));
    }

    @Test
    void shouldRequireAuthenticationForListAndDetail() throws Exception {
        Empleado empleado = seedEmpleado("Nombre Q3", "Direccion Q3", "Telefono Q3");

        mockMvc.perform(get("/api/v1/empleados"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/empleados/{clave}", "EMP-" + empleado.getClaveNumero()))
            .andExpect(status().isUnauthorized());
    }
}
