package com.crudempleados.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.model.Empleado;
import com.crudempleados.support.AbstractApiIntegrationTest;
import org.junit.jupiter.api.Test;

class EmpleadoQueryIT extends AbstractApiIntegrationTest {

    @Test
    void shouldRejectInvalidPaginationParameters() throws Exception {
        mockMvc.perform(get("/api/v1/empleados")
                .param("page", "-1")
                .param("size", "10")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("page")));

        mockMvc.perform(get("/api/v1/empleados")
                .param("page", "0")
                .param("size", "0")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("size")));

        mockMvc.perform(get("/api/v1/empleados")
                .param("page", "0")
                .param("size", "101")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("size")));
    }

    @Test
    void shouldReturnFixedOrderAndEmptyPageBeyondRange() throws Exception {
        Empleado first = seedEmpleado("Nombre O1", "Direccion O1", "Telefono O1");
        Empleado second = seedEmpleado("Nombre O2", "Direccion O2", "Telefono O2");
        seedEmpleado("Nombre O3", "Direccion O3", "Telefono O3");

        mockMvc.perform(get("/api/v1/empleados")
                .param("page", "0")
                .param("size", "2")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].clave", endsWith(String.valueOf(first.getClaveNumero()))))
            .andExpect(jsonPath("$.data[1].clave", endsWith(String.valueOf(second.getClaveNumero()))))
            .andExpect(jsonPath("$.pagination.totalElements").value(3))
            .andExpect(jsonPath("$.pagination.totalPages").value(2));

        mockMvc.perform(get("/api/v1/empleados")
                .param("page", "2")
                .param("size", "2")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0))
            .andExpect(jsonPath("$.pagination.page").value(2))
            .andExpect(jsonPath("$.pagination.size").value(2))
            .andExpect(jsonPath("$.pagination.totalPages").value(2));
    }

    @Test
    void shouldReturnNotFoundForUnknownClave() throws Exception {
        mockMvc.perform(get("/api/v1/empleados/{clave}", "EMP-99999")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    void shouldRejectInvalidClaveWithFieldAndRuleMessage() throws Exception {
        mockMvc.perform(get("/api/v1/empleados/{clave}", "EMP-0007")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("clave")))
            .andExpect(jsonPath("$.message", containsString("EMP-{numero}")));
    }
}
