package com.crudempleados.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.crudempleados.domain.ClaveEmpleadoCodec;
import com.crudempleados.dto.EmpleadoResponse;
import com.crudempleados.model.Empleado;
import org.junit.jupiter.api.Test;

class EmpleadoMapperTest {

    private final EmpleadoMapper mapper = new EmpleadoMapper(new ClaveEmpleadoCodec());

    @Test
    void shouldIncludeDepartamentoClaveWhenEmpleadoIsAssigned() {
        Empleado empleado = new Empleado();
        empleado.setClavePrefijo("EMP-");
        empleado.setClaveNumero(3L);
        empleado.setNombre("Ana Flujo");
        empleado.setDireccion("Calle Flujo 100");
        empleado.setTelefono("5510101010");
        empleado.setDepartamentoClavePrefijo("DEP-");
        empleado.setDepartamentoClaveNumero(7L);

        EmpleadoResponse response = mapper.toResponse(empleado);

        assertThat(response.getClave()).isEqualTo("EMP-3");
        assertThat(response.getDepartamentoClave()).isEqualTo("DEP-7");
    }

    @Test
    void shouldReturnNullDepartamentoClaveWhenEmpleadoHasNoAssignment() {
        Empleado empleado = new Empleado();
        empleado.setClavePrefijo("EMP-");
        empleado.setClaveNumero(4L);
        empleado.setNombre("Sin Depto");
        empleado.setDireccion("Calle 1");
        empleado.setTelefono("5510100000");

        EmpleadoResponse response = mapper.toResponse(empleado);

        assertThat(response.getClave()).isEqualTo("EMP-4");
        assertThat(response.getDepartamentoClave()).isNull();
    }
}
