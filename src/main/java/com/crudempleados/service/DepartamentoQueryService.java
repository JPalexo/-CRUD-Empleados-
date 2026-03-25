package com.crudempleados.service;

import com.crudempleados.domain.ClaveDepartamentoCodec;
import com.crudempleados.domain.exception.ResourceNotFoundException;
import com.crudempleados.dto.DepartamentoPageResponse;
import com.crudempleados.dto.DepartamentoResponse;
import com.crudempleados.dto.PaginationMetadata;
import com.crudempleados.model.Departamento;
import com.crudempleados.model.DepartamentoId;
import com.crudempleados.repository.DepartamentoRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartamentoQueryService {

    private final DepartamentoRepository departamentoRepository;
    private final ClaveDepartamentoCodec claveCodec;
    private final DepartamentoMapper departamentoMapper;
    private final DepartamentoCapacityService departamentoCapacityService;

    public DepartamentoQueryService(
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

    @Transactional(readOnly = true)
    public DepartamentoPageResponse list(int page, int size) {
        if (size > 100) {
            throw new IllegalArgumentException("Field 'size' must be <= 100.");
        }
        Page<Departamento> departamentosPage = departamentoRepository.findAllByOrderByClaveNumeroAsc(PageRequest.of(page, size));

        List<DepartamentoResponse> data = new ArrayList<>();
        for (Departamento departamento : departamentosPage.getContent()) {
            DepartamentoId id = new DepartamentoId(departamento.getClavePrefijo(), departamento.getClaveNumero());
            departamentoCapacityService.assertCapacityInvariant(id);
            data.add(departamentoMapper.toResponse(departamento, departamentoCapacityService.buildMetrics(id)));
        }

        PaginationMetadata pagination = new PaginationMetadata(
            departamentosPage.getNumber(),
            departamentosPage.getSize(),
            departamentosPage.getTotalElements(),
            departamentosPage.getTotalPages()
        );
        return new DepartamentoPageResponse(data, pagination);
    }

    @Transactional(readOnly = true)
    public DepartamentoResponse getByClave(String clave) {
        DepartamentoId id = claveCodec.parse(clave);
        Departamento departamento = departamentoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Departamento not found for clave " + clave));
        departamentoCapacityService.assertCapacityInvariant(id);
        return departamentoMapper.toResponse(departamento, departamentoCapacityService.buildMetrics(id));
    }
}
