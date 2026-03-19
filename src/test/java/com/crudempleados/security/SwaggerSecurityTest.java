package com.crudempleados.security;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.support.AbstractApiIntegrationTest;
import org.junit.jupiter.api.Test;

class SwaggerSecurityTest extends AbstractApiIntegrationTest {

    @Test
    void shouldReturn401ForSwaggerAndOpenApiWithoutCredentials() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/empleados"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowSwaggerAndOpenApiWithCredentials() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk());

        mockMvc.perform(get("/v3/api-docs")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue()))
            .andExpect(status().isOk());
    }

    @Test
    void shouldKeepAdminCrudProtectedWhileEmployeeLoginRouteRemainsPublic() throws Exception {
        mockMvc.perform(post("/api/v1/empleados/login")
                .contentType(APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/empleados"))
            .andExpect(status().isUnauthorized());
    }
}
