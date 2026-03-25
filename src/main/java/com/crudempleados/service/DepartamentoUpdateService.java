package com.crudempleados.service;

import com.crudempleados.domain.ClaveDepartamentoCodec;
import com.crudempleados.domain.exception.DepartamentoDomainConflictException;
import com.crudempleados.domain.exception.ResourceNotFoundException;
import com.crudempleados.dto.DepartamentoResponse;
import com.crudempleados.dto.DepartamentoUpdateRequest;
import com.crudempleados.model.Departamento;
import com.crudempleados.model.DepartamentoId;
import com.crudempleados.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartamentoUpdateService {

    private final DepartamentoRepository departamentoRepository;
    private final ClaveDepartamentoCodec claveCodec;
    private final DepartamentoMapper departamentoMapper;
    private final DepartamentoCapacityService departamentoCapacityService;

    public DepartamentoUpdateService(
        DepartamentoRepository departamentoRepository,
        ClaveDepartamentoCodec claveCodec,
        DepartamentoMapper departamentoMapper,
        DepartamentoCapacityService departamentoCapacityService
    ) {
        this.departamentoRepository = departamentoRepository;
        this.claveCodec = claveCodec;
        this.departamentoMapper = departamentoMapper;
        this.departamentoCapacityService = departamentoCapacityService;
    }

    @Transactional
    public DepartamentoResponse update(String clave, DepartamentoUpdateRequest request) {
        DepartamentoId id = claveCodec.parse(clave);
        Departamento departamento = departamentoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Departamento not found for clave " + clave));

        String normalizedName = normalize(request.getNombre());
        if (departamentoRepository.existsByNombreNormalizadoAndClaveNumeroNot(normalizedName, id.getClaveNumero())) {
            throw new DepartamentoDomainConflictException("Departamento name already exists.");
        }

        departamento.setNombre(request.getNombre().trim());
        departamento.setNombreNormalizado(normalizedName);
        Departamento saved = departamentoRepository.save(departamento);
        return departamentoMapper.toResponse(saved, departamentoCapacityService.buildMetrics(id));
    }

    private String normalize(String nombre) {
        return nombre.trim().toLowerCase();
    }
}
