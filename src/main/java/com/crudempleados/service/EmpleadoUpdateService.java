package com.crudempleados.service;

import com.crudempleados.domain.ClaveDepartamentoCodec;
import com.crudempleados.domain.ClaveEmpleadoCodec;
import com.crudempleados.domain.exception.ResourceNotFoundException;
import com.crudempleados.dto.EmpleadoResponse;
import com.crudempleados.dto.EmpleadoUpdateRequest;
import com.crudempleados.model.DepartamentoId;
import com.crudempleados.model.Empleado;
import com.crudempleados.repository.DepartamentoRepository;
import com.crudempleados.model.EmpleadoId;
import com.crudempleados.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class EmpleadoUpdateService {

    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ClaveDepartamentoCodec claveDepartamentoCodec;
    private final ClaveEmpleadoCodec claveCodec;
    private final EmpleadoMapper empleadoMapper;

    public EmpleadoUpdateService(
        EmpleadoRepository empleadoRepository,
        DepartamentoRepository departamentoRepository,
        ClaveDepartamentoCodec claveDepartamentoCodec,
        ClaveEmpleadoCodec claveCodec,
        EmpleadoMapper empleadoMapper
    ) {
        this.empleadoRepository = empleadoRepository;
        this.departamentoRepository = departamentoRepository;
        this.claveDepartamentoCodec = claveDepartamentoCodec;
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

        if (StringUtils.hasText(request.getDepartamentoClave())) {
            DepartamentoId departamentoId = claveDepartamentoCodec.parse(request.getDepartamentoClave());
            departamentoRepository.findById(departamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento not found for clave " + request.getDepartamentoClave()));
            empleado.setDepartamentoClavePrefijo(departamentoId.getClavePrefijo());
            empleado.setDepartamentoClaveNumero(departamentoId.getClaveNumero());
        }

        Empleado saved = empleadoRepository.save(empleado);
        return empleadoMapper.toResponse(saved);
    }
}
