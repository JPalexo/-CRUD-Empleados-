package com.crudempleados.model;

import java.time.OffsetDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "empleado_login_eventos")
public class EmpleadoLoginEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email_normalizado_input", nullable = false, length = 254)
    private String emailNormalizadoInput;

    @Column(name = "clave_prefijo", length = 4)
    private String clavePrefijo;

    @Column(name = "clave_numero")
    private Long claveNumero;

    @Column(name = "resultado", nullable = false, length = 16)
    private String resultado;

    @Column(name = "motivo", nullable = false, length = 64)
    private String motivo;

    @Column(name = "ocurrido_en", nullable = false)
    private OffsetDateTime ocurridoEn;

    @PrePersist
    void prePersist() {
        if (ocurridoEn == null) {
            ocurridoEn = OffsetDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailNormalizadoInput() {
        return emailNormalizadoInput;
    }

    public void setEmailNormalizadoInput(String emailNormalizadoInput) {
        this.emailNormalizadoInput = emailNormalizadoInput;
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

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public OffsetDateTime getOcurridoEn() {
        return ocurridoEn;
    }

    public void setOcurridoEn(OffsetDateTime ocurridoEn) {
        this.ocurridoEn = ocurridoEn;
    }
}