package com.crudempleados.model;

import java.io.Serializable;
import java.util.Objects;

public class EmpleadoId implements Serializable {

    private String clavePrefijo;
    private Long claveNumero;

    public EmpleadoId() {
    }

    public EmpleadoId(String clavePrefijo, Long claveNumero) {
        this.clavePrefijo = clavePrefijo;
        this.claveNumero = claveNumero;
    }

    public String getClavePrefijo() {
        return clavePrefijo;
    }

    public void setClavePrefijo(String clavePrefijo) {
        this.clavePrefijo = clavePrefijo;
    }

    public Long getClaveNumero() {
        return claveNumero;
    }

    public void setClaveNumero(Long claveNumero) {
        this.claveNumero = claveNumero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmpleadoId empleadoId = (EmpleadoId) o;
        return Objects.equals(clavePrefijo, empleadoId.clavePrefijo)
            && Objects.equals(claveNumero, empleadoId.claveNumero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clavePrefijo, claveNumero);
    }
}
