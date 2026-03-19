package com.crudempleados.model;

import com.crudempleados.domain.ClaveEmpleadoCodec;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "empleados")
@IdClass(EmpleadoId.class)
public class Empleado {

    @Id
    @Column(name = "clave_prefijo", nullable = false, length = 4, updatable = false)
    private String clavePrefijo;

    @Id
    @Column(name = "clave_numero", nullable = false, updatable = false)
    private Long claveNumero;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "direccion", nullable = false, length = 100)
    private String direccion;

    @Column(name = "telefono", nullable = false, length = 100)
    private String telefono;

    @PrePersist
    void assignPrefix() {
        if (clavePrefijo == null || clavePrefijo.isBlank()) {
            clavePrefijo = ClaveEmpleadoCodec.PREFIX;
        }
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
}
