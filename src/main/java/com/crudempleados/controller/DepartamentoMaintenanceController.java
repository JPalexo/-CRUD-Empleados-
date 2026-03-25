package com.crudempleados.controller;

import com.crudempleados.config.ApiVersionConfig;
import com.crudempleados.domain.ClaveDepartamentoCodec;
import com.crudempleados.dto.DepartamentoResponse;
import com.crudempleados.dto.DepartamentoUpdateRequest;
import com.crudempleados.service.DepartamentoDeleteService;
import com.crudempleados.service.DepartamentoUpdateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(ApiVersionConfig.DEPARTAMENTOS_V1_BASE_PATH)
public class DepartamentoMaintenanceController {

    private final DepartamentoUpdateService departamentoUpdateService;
    private final DepartamentoDeleteService departamentoDeleteService;

    public DepartamentoMaintenanceController(
        DepartamentoUpdateService departamentoUpdateService,
        DepartamentoDeleteService departamentoDeleteService
    ) {
        this.departamentoUpdateService = departamentoUpdateService;
        this.departamentoDeleteService = departamentoDeleteService;
    }

    @PutMapping("/{clave}")
    public DepartamentoResponse update(
        @PathVariable
        @Pattern(regexp = ClaveDepartamentoCodec.CLAVE_REGEX, message = "must match DEP-{numero} without leading zeros")
        String clave,
        @Valid @RequestBody DepartamentoUpdateRequest request
    ) {
        return departamentoUpdateService.update(clave, request);
    }

    @DeleteMapping("/{clave}")
    public ResponseEntity<Void> delete(
        @PathVariable
        @Pattern(regexp = ClaveDepartamentoCodec.CLAVE_REGEX, message = "must match DEP-{numero} without leading zeros")
        String clave
    ) {
        departamentoDeleteService.delete(clave);
        return ResponseEntity.noContent().build();
    }
}
