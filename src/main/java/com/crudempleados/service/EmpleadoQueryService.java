package com.crudempleados.service;

import com.crudempleados.domain.ClaveEmpleadoCodec;
import com.crudempleados.domain.exception.ResourceNotFoundException;
import com.crudempleados.dto.EmpleadoPageResponse;
import com.crudempleados.dto.PaginationMetadata;
import com.crudempleados.dto.EmpleadoResponse;
import com.crudempleados.model.Empleado;
import com.crudempleados.model.EmpleadoId;
import com.crudempleados.repository.EmpleadoRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpleadoQueryService {

    private final EmpleadoRepository empleadoRepository;
    private final ClaveEmpleadoCodec claveCodec;
    private final EmpleadoMapper empleadoMapper;

    public EmpleadoQueryService(EmpleadoRepository empleadoRepository, ClaveEmpleadoCodec claveCodec, EmpleadoMapper empleadoMapper) {
        this.empleadoRepository = empleadoRepository;
        this.claveCodec = claveCodec;
        this.empleadoMapper = empleadoMapper;
    }

    @Transactional(readOnly = true)
    public EmpleadoPageResponse list(int page, int size) {
        Page<Empleado> empleadosPage = empleadoRepository.findAllByOrderByClaveNumeroAsc(PageRequest.of(page, size));

        List<EmpleadoResponse> data = new ArrayList<>();
        for (Empleado empleado : empleadosPage.getContent()) {
            data.add(empleadoMapper.toResponse(empleado));
        }

        PaginationMetadata pagination = new PaginationMetadata(
            empleadosPage.getNumber(),
            empleadosPage.getSize(),
            empleadosPage.getTotalElements(),
            empleadosPage.getTotalPages()
        );

        return new EmpleadoPageResponse(data, pagination);
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse getByClave(String clave) {
        EmpleadoId id = claveCodec.parse(clave);
        Empleado empleado = empleadoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Empleado not found for clave " + clave));
        return empleadoMapper.toResponse(empleado);
    }
}
