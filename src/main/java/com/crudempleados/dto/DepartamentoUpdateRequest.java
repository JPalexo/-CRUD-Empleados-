package com.crudempleados.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = false)
public class DepartamentoUpdateRequest {

    @NotBlank(message = "must not be blank and must satisfy max length 100")
    @Size(max = 100, message = "must satisfy max length 100")
    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}