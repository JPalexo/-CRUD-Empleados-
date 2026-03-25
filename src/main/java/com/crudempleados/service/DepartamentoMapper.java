package com.crudempleados.service;

import com.crudempleados.domain.ClaveDepartamentoCodec;
import com.crudempleados.dto.DepartamentoResponse;
import com.crudempleados.model.Departamento;
import com.crudempleados.model.DepartamentoId;
import com.crudempleados.service.DepartamentoCapacityService.DepartamentoMetricas;
import org.springframework.stereotype.Component;

@Component
public class DepartamentoMapper {

    private final ClaveDepartamentoCodec claveCodec;

    public DepartamentoMapper(ClaveDepartamentoCodec claveCodec) {
        this.claveCodec = claveCodec;
    }

    public DepartamentoResponse toResponse(Departamento departamento, DepartamentoMetricas metricas) {
        DepartamentoId id = new DepartamentoId(departamento.getClavePrefijo(), departamento.getClaveNumero());
        return new DepartamentoResponse(
            claveCodec.format(id),
            departamento.getNombre(),
            metricas.ocupacionActual(),
            metricas.capacidadMaxima(),
            metricas.porcentajeOcupacion(),
            metricas.estaLleno()
        );
    }
}
