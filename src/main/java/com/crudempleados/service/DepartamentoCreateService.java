package com.crudempleados.service;

import com.crudempleados.domain.ClaveDepartamentoCodec;
import com.crudempleados.domain.exception.DepartamentoDomainConflictException;
import com.crudempleados.dto.DepartamentoCreateRequest;
import com.crudempleados.dto.DepartamentoResponse;
import com.crudempleados.model.Departamento;
import com.crudempleados.model.DepartamentoId;
import com.crudempleados.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartamentoCreateService {

    private final DepartamentoRepository departamentoRepository;
    private final DepartamentoMapper departamentoMapper;
    private final DepartamentoCapacityService departamentoCapacityService;

    public DepartamentoCreateService(
        DepartamentoRepository departamentoRepository,
        DepartamentoMapper departamentoMapper,
        DepartamentoCapacityService departamentoCapacityService
    ) {
        this.departamentoRepository = departamentoRepository;
        this.departamentoMapper = departamentoMapper;
        this.departamentoCapacityService = departamentoCapacityService;
    }

    @Transactional
    public DepartamentoResponse create(DepartamentoCreateRequest request) {
        String normalizedName = normalize(request.getNombre());
        if (departamentoRepository.existsByNombreNormalizado(normalizedName)) {
            throw new DepartamentoDomainConflictException("Departamento name already exists.");
        }

        Departamento departamento = new Departamento();
        departamento.setClavePrefijo(ClaveDepartamentoCodec.PREFIX);
        departamento.setClaveNumero(departamentoRepository.nextClaveNumero());
        departamento.setNombre(request.getNombre().trim());
        departamento.setNombreNormalizado(normalizedName);
        Departamento saved = departamentoRepository.save(departamento);

        DepartamentoId id = new DepartamentoId(saved.getClavePrefijo(), saved.getClaveNumero());
        return departamentoMapper.toResponse(saved, departamentoCapacityService.buildMetrics(id));
    }

    private String normalize(String nombre) {
        return nombre.trim().toLowerCase();
    }
}
