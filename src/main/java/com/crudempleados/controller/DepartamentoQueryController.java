package com.crudempleados.controller;

import com.crudempleados.config.ApiVersionConfig;
import com.crudempleados.domain.ClaveDepartamentoCodec;
import com.crudempleados.dto.DepartamentoPageResponse;
import com.crudempleados.dto.DepartamentoResponse;
import com.crudempleados.service.DepartamentoQueryService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(ApiVersionConfig.DEPARTAMENTOS_V1_BASE_PATH)
public class DepartamentoQueryController {

    private final DepartamentoQueryService departamentoQueryService;

    public DepartamentoQueryController(DepartamentoQueryService departamentoQueryService) {
        this.departamentoQueryService = departamentoQueryService;
    }

    @GetMapping
    public DepartamentoPageResponse listAll(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        return departamentoQueryService.list(page, size);
    }

    @GetMapping("/{clave}")
    public DepartamentoResponse getByClave(
        @PathVariable
        @Pattern(regexp = ClaveDepartamentoCodec.CLAVE_REGEX, message = "must match DEP-{numero} without leading zeros")
        String clave
    ) {
        return departamentoQueryService.getByClave(clave);
    }
}
