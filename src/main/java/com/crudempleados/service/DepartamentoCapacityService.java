package com.crudempleados.service;

import com.crudempleados.domain.exception.DepartamentoDomainConflictException;
import com.crudempleados.model.DepartamentoId;
import com.crudempleados.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartamentoCapacityService {

    public static final int CAPACIDAD_MAXIMA = 3;

    private final EmpleadoRepository empleadoRepository;

    public DepartamentoCapacityService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional(readOnly = true)
    public DepartamentoMetricas buildMetrics(DepartamentoId id) {
        long ocupacion = empleadoRepository.countByDepartamentoClavePrefijoAndDepartamentoClaveNumero(
            id.getClavePrefijo(),
            id.getClaveNumero()
        );
        boolean estaLleno = ocupacion >= CAPACIDAD_MAXIMA;
        double porcentaje = truncateToTwoDecimals((ocupacion * 100.0) / CAPACIDAD_MAXIMA);
        return new DepartamentoMetricas(ocupacion, CAPACIDAD_MAXIMA, porcentaje, estaLleno);
    }

    @Transactional(readOnly = true)
    public void assertDeletionAllowed(DepartamentoId id) {
        long ocupacion = empleadoRepository.countByDepartamentoClavePrefijoAndDepartamentoClaveNumero(
            id.getClavePrefijo(),
            id.getClaveNumero()
        );
        if (ocupacion > 0) {
            throw new DepartamentoDomainConflictException("Departamento in use and cannot be deleted.");
        }
    }

    @Transactional(readOnly = true)
    public void assertCapacityInvariant(DepartamentoId id) {
        long ocupacion = empleadoRepository.countByDepartamentoClavePrefijoAndDepartamentoClaveNumero(
            id.getClavePrefijo(),
            id.getClaveNumero()
        );
        if (ocupacion > CAPACIDAD_MAXIMA) {
            throw new DepartamentoDomainConflictException("Departamento occupancy exceeds max capacity.");
        }
    }

    private double truncateToTwoDecimals(double value) {
        return Math.floor(value * 100.0) / 100.0;
    }

    public record DepartamentoMetricas(long ocupacionActual, int capacidadMaxima, double porcentajeOcupacion, boolean estaLleno) {
    }
}
