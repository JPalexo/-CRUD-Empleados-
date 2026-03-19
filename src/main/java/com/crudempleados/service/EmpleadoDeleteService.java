package com.crudempleados.service;

import com.crudempleados.domain.ClaveEmpleadoCodec;
import com.crudempleados.domain.exception.ResourceNotFoundException;
import com.crudempleados.model.Empleado;
import com.crudempleados.model.EmpleadoId;
import com.crudempleados.repository.EmpleadoCredencialRepository;
import com.crudempleados.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpleadoDeleteService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoCredencialRepository empleadoCredencialRepository;
    private final ClaveEmpleadoCodec claveCodec;

    public EmpleadoDeleteService(
        EmpleadoRepository empleadoRepository,
        EmpleadoCredencialRepository empleadoCredencialRepository,
        ClaveEmpleadoCodec claveCodec
    ) {
        this.empleadoRepository = empleadoRepository;
        this.empleadoCredencialRepository = empleadoCredencialRepository;
        this.claveCodec = claveCodec;
    }

    @Transactional
    public void delete(String clave) {
        EmpleadoId id = claveCodec.parse(clave);
        Empleado empleado = empleadoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Empleado not found for clave " + clave));
        empleadoCredencialRepository.findById(id).ifPresent(empleadoCredencialRepository::delete);
        empleadoRepository.delete(empleado);
    }
}
