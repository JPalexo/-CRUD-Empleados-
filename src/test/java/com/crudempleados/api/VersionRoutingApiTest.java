package com.crudempleados.api;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.support.AbstractApiIntegrationTest;
import org.junit.jupiter.api.Test;

class VersionRoutingApiTest extends AbstractApiIntegrationTest {

    @Test
    void shouldRequireAuthenticationForVersionedCrudRoute() throws Exception {
        mockMvc.perform(get("/api/v1/empleados"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404ForUnversionedCrudRouteWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/empleados")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/empleados/EMP-1")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnpublishedVersionRouteWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v2/empleados")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v2/empleados/EMP-1")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldExposeOnlyVersionedEmployeeLoginRoute() throws Exception {
        mockMvc.perform(post("/api/v1/empleados/login")
                .contentType(APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/empleados/login")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/v2/empleados/login")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldExposeOnlyVersionedDepartamentoRoutes() throws Exception {
        mockMvc.perform(get("/api/v1/departamentos")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/departamentos")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v2/departamentos")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isNotFound());
    }
}
