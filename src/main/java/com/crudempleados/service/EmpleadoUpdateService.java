package com.crudempleados.service;

import com.crudempleados.domain.ClaveEmpleadoCodec;
import com.crudempleados.domain.exception.ResourceNotFoundException;
import com.crudempleados.dto.EmpleadoResponse;
import com.crudempleados.dto.EmpleadoUpdateRequest;
import com.crudempleados.model.Empleado;
import com.crudempleados.model.EmpleadoId;
import com.crudempleados.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpleadoUpdateService {

    private final EmpleadoRepository empleadoRepository;
    private final ClaveEmpleadoCodec claveCodec;
    private final EmpleadoMapper empleadoMapper;

    public EmpleadoUpdateService(EmpleadoRepository empleadoRepository, ClaveEmpleadoCodec claveCodec, EmpleadoMapper empleadoMapper) {
        this.empleadoRepository = empleadoRepository;
        this.claveCodec = claveCodec;
        this.empleadoMapper = empleadoMapper;
    }

    @Transactional
    public EmpleadoResponse update(String clave, EmpleadoUpdateRequest request) {
        EmpleadoId id = claveCodec.parse(clave);
        Empleado empleado = empleadoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Empleado not found for clave " + clave));
        empleado.setNombre(request.getNombre().trim());
        empleado.setDireccion(request.getDireccion().trim());
        empleado.setTelefono(request.getTelefono().trim());
        Empleado saved = empleadoRepository.save(empleado);
        return empleadoMapper.toResponse(saved);
    }
}
