package com.crudempleados.dto;

public class DepartamentoResponse {

    private String clave;
    private String nombre;
    private long ocupacionActual;
    private int capacidadMaxima;
    private double porcentajeOcupacion;
    private boolean estaLleno;

    public DepartamentoResponse() {
    }

    public DepartamentoResponse(
        String clave,
        String nombre,
        long ocupacionActual,
        int capacidadMaxima,
        double porcentajeOcupacion,
        boolean estaLleno
    ) {
        this.clave = clave;
        this.nombre = nombre;
        this.ocupacionActual = ocupacionActual;
        this.capacidadMaxima = capacidadMaxima;
        this.porcentajeOcupacion = porcentajeOcupacion;
        this.estaLleno = estaLleno;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getOcupacionActual() {
        return ocupacionActual;
    }

    public void setOcupacionActual(long ocupacionActual) {
        this.ocupacionActual = ocupacionActual;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public double getPorcentajeOcupacion() {
        return porcentajeOcupacion;
    }

    public void setPorcentajeOcupacion(double porcentajeOcupacion) {
        this.porcentajeOcupacion = porcentajeOcupacion;
    }

    public boolean isEstaLleno() {
        return estaLleno;
    }

    public void setEstaLleno(boolean estaLleno) {
        this.estaLleno = estaLleno;
    }
}