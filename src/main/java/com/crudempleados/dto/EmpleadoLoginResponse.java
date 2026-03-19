package com.crudempleados.dto;

public class EmpleadoLoginResponse {

    private boolean authenticated;
    private EmpleadoLoginIdentity empleado;

    public EmpleadoLoginResponse() {
    }

    public EmpleadoLoginResponse(boolean authenticated, EmpleadoLoginIdentity empleado) {
        this.authenticated = authenticated;
        this.empleado = empleado;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public EmpleadoLoginIdentity getEmpleado() {
        return empleado;
    }

    public void setEmpleado(EmpleadoLoginIdentity empleado) {
        this.empleado = empleado;
    }

    public static class EmpleadoLoginIdentity {

        private String clave;
        private String nombre;
        private String email;

        public EmpleadoLoginIdentity() {
        }

        public EmpleadoLoginIdentity(String clave, String nombre, String email) {
            this.clave = clave;
            this.nombre = nombre;
            this.email = email;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}