package com.crudempleados.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = false)
public class EmpleadoCreateRequest {

    @NotBlank(message = "must not be blank and must satisfy max length 100")
    @Size(max = 100, message = "must satisfy max length 100")
    private String nombre;

    @NotBlank(message = "must not be blank and must satisfy max length 100")
    @Size(max = 100, message = "must satisfy max length 100")
    private String direccion;

    @NotBlank(message = "must not be blank and must satisfy max length 100")
    @Size(max = 100, message = "must satisfy max length 100")
    private String telefono;

    @NotBlank(message = "must not be blank")
    @Email(message = "must be a well-formed email address")
    @Size(max = 254, message = "must satisfy max length 254")
    private String email;

    @NotBlank(message = "must not be blank")
    @Size(min = 8, max = 64, message = "must satisfy length between 8 and 64")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "must include at least one letter and one number")
    private String password;

    @NotBlank(message = "must not be blank")
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartamentoClave() {
        return departamentoClave;
    }

    public void setDepartamentoClave(String departamentoClave) {
        this.departamentoClave = departamentoClave;
    }
}
