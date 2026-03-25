package com.crudempleados.api;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.model.Departamento;
import com.crudempleados.model.Empleado;
import com.crudempleados.support.AbstractApiIntegrationTest;
import org.junit.jupiter.api.Test;

class DepartamentoQueryApiTest extends AbstractApiIntegrationTest {

    @Test
    void shouldListAndFetchDepartamentoWithMetrics() throws Exception {
        Departamento first = seedDepartamento("Ventas");
        seedDepartamento("Operaciones");

        Empleado e1 = seedEmpleado("N1", "D1", "T1");
        assignEmpleadoToDepartamento(e1, first);

        mockMvc.perform(get("/api/v1/departamentos")
                .param("page", "0")
                .param("size", "20")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.pagination.page").value(0))
            .andExpect(jsonPath("$.pagination.size").value(20))
            .andExpect(jsonPath("$.data[0].ocupacionActual").value(1))
            .andExpect(jsonPath("$.data[0].capacidadMaxima").value(3));

        mockMvc.perform(get("/api/v1/departamentos/{clave}", "DEP-" + first.getClaveNumero())
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clave").value("DEP-" + first.getClaveNumero()))
            .andExpect(jsonPath("$.ocupacionActual").value(1));
    }

    @Test
    void shouldApplyDefaultPaginationAndRejectSizeAbove100() throws Exception {
        seedDepartamento("DefaultPaginacion");

        mockMvc.perform(get("/api/v1/departamentos")
                .param("page", "0")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pagination.size").value(20));

        mockMvc.perform(get("/api/v1/departamentos")
                .param("page", "0")
                .param("size", "101")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Field 'size' must be <= 100."));
    }

    @Test
    void shouldKeepMetricsConsistentBetweenListAndDetail() throws Exception {
        Departamento departamento = seedDepartamento("Consistencia");
        Empleado e1 = seedEmpleado("NC1", "DC1", "TC1");
        Empleado e2 = seedEmpleado("NC2", "DC2", "TC2");
        assignEmpleadoToDepartamento(e1, departamento);
        assignEmpleadoToDepartamento(e2, departamento);
        String clave = "DEP-" + departamento.getClaveNumero();

        mockMvc.perform(get("/api/v1/departamentos")
                .param("page", "0")
                .param("size", "20")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].clave").value(clave))
            .andExpect(jsonPath("$.data[0].ocupacionActual").value(2))
            .andExpect(jsonPath("$.data[0].porcentajeOcupacion", closeTo(66.66, 0.0001)))
            .andExpect(jsonPath("$.data[0].estaLleno").value(false));

        mockMvc.perform(get("/api/v1/departamentos/{clave}", clave)
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ocupacionActual").value(2))
            .andExpect(jsonPath("$.porcentajeOcupacion", closeTo(66.66, 0.0001)))
            .andExpect(jsonPath("$.estaLleno").value(false));
    }

    @Test
    void shouldTruncatePercentageWithoutRounding() throws Exception {
        Departamento departamento = seedDepartamento("Truncado");
        Empleado e1 = seedEmpleado("NT1", "DT1", "TT1");
        Empleado e2 = seedEmpleado("NT2", "DT2", "TT2");
        assignEmpleadoToDepartamento(e1, departamento);
        assignEmpleadoToDepartamento(e2, departamento);

        mockMvc.perform(get("/api/v1/departamentos/{clave}", "DEP-" + departamento.getClaveNumero())
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.porcentajeOcupacion").value(66.66));
    }
}