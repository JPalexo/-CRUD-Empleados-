package com.crudempleados.security;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.support.AbstractApiIntegrationTest;
import org.junit.jupiter.api.Test;

class DepartamentoSecurityTest extends AbstractApiIntegrationTest {

    @Test
    void shouldRequireAuthenticationForAllDepartamentoEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/departamentos"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/departamentos/DEP-1"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/departamentos")
                .contentType(APPLICATION_JSON)
                .content("{\"nombre\":\"Ventas\"}"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/v1/departamentos/DEP-1")
                .contentType(APPLICATION_JSON)
                .content("{\"nombre\":\"Comercial\"}"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/v1/departamentos/DEP-1"))
            .andExpect(status().isUnauthorized());
    }
}