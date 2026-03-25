package com.crudempleados.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = false)
public class EmpleadoUpdateRequest {

    @NotBlank(message = "must not be blank and must satisfy max length 100")
    @Size(max = 100, message = "must satisfy max length 100")
    private String nombre;

    @NotBlank(message = "must not be blank and must satisfy max length 100")
    @Size(max = 100, message = "must satisfy max length 100")
    private String direccion;

    @NotBlank(message = "must not be blank and must satisfy max length 100")
    @Size(max = 100, message = "must satisfy max length 100")
    private String telefono;

    @Pattern(regexp = "^DEP-[1-9][0-9]*$", message = "must match DEP-{numero} without leading zeros")
    private String departamentoClave;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDepartamentoClave() {
        return departamentoClave;
    }

    public void setDepartamentoClave(String departamentoClave) {
        this.departamentoClave = departamentoClave;
    }
}
