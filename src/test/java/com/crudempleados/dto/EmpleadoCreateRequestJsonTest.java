package com.crudempleados.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class EmpleadoCreateRequestJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldDeserializeDepartamentoClaveField() throws Exception {
        String json = """
            {
              \"nombre\": \"Ana\",
              \"direccion\": \"Calle 1\",
              \"telefono\": \"5511111111\",
              \"email\": \"ana@empresa.com\",
              \"password\": \"abc12345\",
              \"departamentoClave\": \"DEP-1\"
            }
            """;

        EmpleadoCreateRequest request = objectMapper.readValue(json, EmpleadoCreateRequest.class);

        assertThat(request.getDepartamentoClave()).isEqualTo("DEP-1");
    }

    @Test
    void shouldRejectUnknownJsonFields() {
        String json = """
            {
              \"nombre\": \"Ana\",
              \"direccion\": \"Calle 1\",
              \"telefono\": \"5511111111\",
              \"email\": \"ana@empresa.com\",
              \"password\": \"abc12345\",
              \"departamento_clave\": \"DEP-1\"
            }
            """;

        assertThatThrownBy(() -> objectMapper.readValue(json, EmpleadoCreateRequest.class))
            .hasMessageContaining("departamento_clave");
    }
}
