package com.crudempleados.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.crudempleados.model.Departamento;
import com.crudempleados.model.DepartamentoId;
import com.crudempleados.model.Empleado;
import com.crudempleados.repository.EmpleadoRepository;
import com.crudempleados.service.DepartamentoCapacityService;
import com.crudempleados.support.AbstractApiIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DepartamentoOcupacionIT extends AbstractApiIntegrationTest {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private DepartamentoCapacityService departamentoCapacityService;

    @Test
    void shouldCountOccupancyUsingCompositeDepartmentKey() {
        Departamento ventas = seedDepartamento("Ventas IT");
        Departamento operaciones = seedDepartamento("Operaciones IT");

        Empleado e1 = seedEmpleado("N1", "D1", "T1");
        Empleado e2 = seedEmpleado("N2", "D2", "T2");
        Empleado e3 = seedEmpleado("N3", "D3", "T3");

        assignEmpleadoToDepartamento(e1, ventas);
        assignEmpleadoToDepartamento(e2, ventas);
        assignEmpleadoToDepartamento(e3, operaciones);

        long ventasCount = empleadoRepository.countByDepartamentoClavePrefijoAndDepartamentoClaveNumero(
            ventas.getClavePrefijo(),
            ventas.getClaveNumero()
        );
        long operacionesCount = empleadoRepository.countByDepartamentoClavePrefijoAndDepartamentoClaveNumero(
            operaciones.getClavePrefijo(),
            operaciones.getClaveNumero()
        );

        assertThat(ventasCount).isEqualTo(2L);
        assertThat(operacionesCount).isEqualTo(1L);

        DepartamentoCapacityService.DepartamentoMetricas metricas = departamentoCapacityService.buildMetrics(
            new DepartamentoId(ventas.getClavePrefijo(), ventas.getClaveNumero())
        );
        assertThat(metricas.ocupacionActual()).isEqualTo(2L);
        assertThat(metricas.capacidadMaxima()).isEqualTo(3);
        assertThat(metricas.porcentajeOcupacion()).isEqualTo(66.66);
        assertThat(metricas.estaLleno()).isFalse();
    }
}
