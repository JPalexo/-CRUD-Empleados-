package com.crudempleados.service;

import com.crudempleados.domain.ClaveDepartamentoCodec;
import com.crudempleados.domain.ClaveEmpleadoCodec;
import com.crudempleados.domain.exception.ResourceNotFoundException;
import com.crudempleados.dto.EmpleadoCreateRequest;
import com.crudempleados.dto.EmpleadoResponse;
import com.crudempleados.model.DepartamentoId;
import com.crudempleados.model.Empleado;
import com.crudempleados.repository.DepartamentoRepository;
import com.crudempleados.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpleadoCreateService {

    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ClaveDepartamentoCodec claveDepartamentoCodec;
    private final EmpleadoMapper empleadoMapper;
    private final EmpleadoCredentialService empleadoCredentialService;

    public EmpleadoCreateService(
        EmpleadoRepository empleadoRepository,
        DepartamentoRepository departamentoRepository,
        ClaveDepartamentoCodec claveDepartamentoCodec,
        EmpleadoMapper empleadoMapper,
        EmpleadoCredentialService empleadoCredentialService
    ) {
        this.empleadoRepository = empleadoRepository;
        this.departamentoRepository = departamentoRepository;
        this.claveDepartamentoCodec = claveDepartamentoCodec;
        this.empleadoMapper = empleadoMapper;
        this.empleadoCredentialService = empleadoCredentialService;
    }

    @Transactional
    public EmpleadoResponse create(EmpleadoCreateRequest request) {
        DepartamentoId departamentoId = claveDepartamentoCodec.parse(request.getDepartamentoClave());
        departamentoRepository.findById(departamentoId)
            .orElseThrow(() -> new ResourceNotFoundException("Departamento not found for clave " + request.getDepartamentoClave()));

        Empleado empleado = new Empleado();
        empleado.setClavePrefijo(ClaveEmpleadoCodec.PREFIX);
        empleado.setClaveNumero(empleadoRepository.nextClaveNumero());
        empleado.setNombre(request.getNombre().trim());
        empleado.setDireccion(request.getDireccion().trim());
        empleado.setTelefono(request.getTelefono().trim());
        empleado.setDepartamentoClavePrefijo(departamentoId.getClavePrefijo());
        empleado.setDepartamentoClaveNumero(departamentoId.getClaveNumero());

        Empleado saved = empleadoRepository.save(empleado);
        empleadoCredentialService.createCredentialFor(saved, request.getEmail(), request.getPassword());
        return empleadoMapper.toResponse(saved);
    }
}
