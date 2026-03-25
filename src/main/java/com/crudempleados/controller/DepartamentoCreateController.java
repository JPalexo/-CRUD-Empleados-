package com.crudempleados.controller;

import com.crudempleados.config.ApiVersionConfig;
import com.crudempleados.dto.DepartamentoCreateRequest;
import com.crudempleados.dto.DepartamentoResponse;
import com.crudempleados.service.DepartamentoCreateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.DEPARTAMENTOS_V1_BASE_PATH)
public class DepartamentoCreateController {

    private final DepartamentoCreateService departamentoCreateService;

    public DepartamentoCreateController(DepartamentoCreateService departamentoCreateService) {
        this.departamentoCreateService = departamentoCreateService;
    }

    @PostMapping
    public ResponseEntity<DepartamentoResponse> create(@Valid @RequestBody DepartamentoCreateRequest request) {
        DepartamentoResponse response = departamentoCreateService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
