package com.crudempleados.controller;

import com.crudempleados.config.ApiVersionConfig;
import com.crudempleados.dto.EmpleadoCreateRequest;
import com.crudempleados.dto.EmpleadoResponse;
import com.crudempleados.service.EmpleadoCreateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.EMPLEADOS_V1_BASE_PATH)
public class EmpleadoCreateController {

    private final EmpleadoCreateService empleadoCreateService;

    public EmpleadoCreateController(EmpleadoCreateService empleadoCreateService) {
        this.empleadoCreateService = empleadoCreateService;
    }

    @PostMapping
    public ResponseEntity<EmpleadoResponse> create(@Valid @RequestBody EmpleadoCreateRequest request) {
        EmpleadoResponse response = empleadoCreateService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
