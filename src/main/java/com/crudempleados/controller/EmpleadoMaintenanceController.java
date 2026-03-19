package com.crudempleados.controller;

import com.crudempleados.config.ApiVersionConfig;
import com.crudempleados.domain.ClaveEmpleadoCodec;
import com.crudempleados.dto.EmpleadoResponse;
import com.crudempleados.dto.EmpleadoUpdateRequest;
import com.crudempleados.service.EmpleadoDeleteService;
import com.crudempleados.service.EmpleadoUpdateService;
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
@RequestMapping(ApiVersionConfig.EMPLEADOS_V1_BASE_PATH)
public class EmpleadoMaintenanceController {

    private final EmpleadoUpdateService empleadoUpdateService;
    private final EmpleadoDeleteService empleadoDeleteService;

    public EmpleadoMaintenanceController(EmpleadoUpdateService empleadoUpdateService, EmpleadoDeleteService empleadoDeleteService) {
        this.empleadoUpdateService = empleadoUpdateService;
        this.empleadoDeleteService = empleadoDeleteService;
    }

    @PutMapping("/{clave}")
    public EmpleadoResponse update(
        @PathVariable
        @Pattern(regexp = ClaveEmpleadoCodec.CLAVE_REGEX, message = "must match EMP-{numero} without leading zeros")
        String clave,
        @Valid @RequestBody EmpleadoUpdateRequest request
    ) {
        return empleadoUpdateService.update(clave, request);
    }

    @DeleteMapping("/{clave}")
    public ResponseEntity<Void> delete(
        @PathVariable
        @Pattern(regexp = ClaveEmpleadoCodec.CLAVE_REGEX, message = "must match EMP-{numero} without leading zeros")
        String clave
    ) {
        empleadoDeleteService.delete(clave);
        return ResponseEntity.noContent().build();
    }
}
