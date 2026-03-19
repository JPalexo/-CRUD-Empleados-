package com.crudempleados.controller;

import com.crudempleados.config.ApiVersionConfig;
import com.crudempleados.domain.ClaveEmpleadoCodec;
import com.crudempleados.dto.EmpleadoPageResponse;
import com.crudempleados.dto.EmpleadoResponse;
import com.crudempleados.service.EmpleadoQueryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(ApiVersionConfig.EMPLEADOS_V1_BASE_PATH)
public class EmpleadoQueryController {

    private final EmpleadoQueryService empleadoQueryService;

    public EmpleadoQueryController(EmpleadoQueryService empleadoQueryService) {
        this.empleadoQueryService = empleadoQueryService;
    }

    @GetMapping
    public EmpleadoPageResponse listAll(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return empleadoQueryService.list(page, size);
    }

    @GetMapping("/{clave}")
    public EmpleadoResponse getByClave(
        @PathVariable
        @Pattern(regexp = ClaveEmpleadoCodec.CLAVE_REGEX, message = "must match EMP-{numero} without leading zeros")
        String clave
    ) {
        return empleadoQueryService.getByClave(clave);
    }
}
