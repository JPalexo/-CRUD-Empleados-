package com.crudempleados.controller;

import com.crudempleados.config.ApiVersionConfig;
import com.crudempleados.dto.EmpleadoLoginRequest;
import com.crudempleados.dto.EmpleadoLoginResponse;
import com.crudempleados.service.EmpleadoLoginService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.EMPLEADOS_LOGIN_V1_PATH)
public class EmpleadoLoginController {

    private final EmpleadoLoginService empleadoLoginService;

    public EmpleadoLoginController(EmpleadoLoginService empleadoLoginService) {
        this.empleadoLoginService = empleadoLoginService;
    }

    @Operation(summary = "Login simbolico de empleado", security = {})
    @PostMapping
    public ResponseEntity<EmpleadoLoginResponse> login(@Valid @RequestBody EmpleadoLoginRequest request) {
        EmpleadoLoginResponse response = empleadoLoginService.login(request);
        return ResponseEntity.ok(response);
    }
}