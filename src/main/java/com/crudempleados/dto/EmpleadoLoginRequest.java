package com.crudempleados.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = false)
public class EmpleadoLoginRequest {

    @NotBlank(message = "must not be blank")
    @Email(message = "must be a well-formed email address")
    @Size(max = 254, message = "must satisfy max length 254")
    private String email;

    @NotBlank(message = "must not be blank")
    @Size(min = 8, max = 64, message = "must satisfy length between 8 and 64")
    private String password;

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
}