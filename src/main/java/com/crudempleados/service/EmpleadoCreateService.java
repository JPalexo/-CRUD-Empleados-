package com.crudempleados.service;

import com.crudempleados.domain.ClaveEmpleadoCodec;
import com.crudempleados.dto.EmpleadoCreateRequest;
import com.crudempleados.dto.EmpleadoResponse;
import com.crudempleados.model.Empleado;
import com.crudempleados.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpleadoCreateService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoMapper empleadoMapper;
    private final EmpleadoCredentialService empleadoCredentialService;

    public EmpleadoCreateService(
        EmpleadoRepository empleadoRepository,
        EmpleadoMapper empleadoMapper,
        EmpleadoCredentialService empleadoCredentialService
    ) {
        this.empleadoRepository = empleadoRepository;
        this.empleadoMapper = empleadoMapper;
        this.empleadoCredentialService = empleadoCredentialService;
    }

    @Transactional
    public EmpleadoResponse create(EmpleadoCreateRequest request) {
        Empleado empleado = new Empleado();
        empleado.setClavePrefijo(ClaveEmpleadoCodec.PREFIX);
        empleado.setClaveNumero(empleadoRepository.nextClaveNumero());
        empleado.setNombre(request.getNombre().trim());
        empleado.setDireccion(request.getDireccion().trim());
        empleado.setTelefono(request.getTelefono().trim());

        Empleado saved = empleadoRepository.save(empleado);
        empleadoCredentialService.createCredentialFor(saved, request.getEmail(), request.getPassword());
        return empleadoMapper.toResponse(saved);
    }
}
