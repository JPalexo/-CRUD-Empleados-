package com.crudempleados.service;

import com.crudempleados.domain.ClaveDepartamentoCodec;
import com.crudempleados.domain.exception.ResourceNotFoundException;
import com.crudempleados.model.Departamento;
import com.crudempleados.model.DepartamentoId;
import com.crudempleados.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartamentoDeleteService {

    private final DepartamentoRepository departamentoRepository;
    private final ClaveDepartamentoCodec claveCodec;
    private final DepartamentoCapacityService departamentoCapacityService;

    public DepartamentoDeleteService(
        DepartamentoRepository departamentoRepository,
        ClaveDepartamentoCodec claveCodec,
        DepartamentoCapacityService departamentoCapacityService
    ) {
        this.departamentoRepository = departamentoRepository;
        this.claveCodec = claveCodec;
        this.departamentoCapacityService = departamentoCapacityService;
    }

    @Transactional
    public void delete(String clave) {
        DepartamentoId id = claveCodec.parse(clave);
        Departamento departamento = departamentoRepository.findByIdForUpdate(id.getClavePrefijo(), id.getClaveNumero())
            .orElseThrow(() -> new ResourceNotFoundException("Departamento not found for clave " + clave));
        departamentoCapacityService.assertDeletionAllowed(id);
        departamentoRepository.delete(departamento);
    }
}
