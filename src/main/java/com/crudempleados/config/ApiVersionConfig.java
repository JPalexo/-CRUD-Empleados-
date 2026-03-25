package com.crudempleados.config;

public final class ApiVersionConfig {

    public static final String API_BASE_PATH = "/api";
    public static final String API_V1_BASE_PATH = API_BASE_PATH + "/v1";
    public static final String EMPLEADOS_V1_BASE_PATH = API_V1_BASE_PATH + "/empleados";
    public static final String EMPLEADOS_LOGIN_V1_PATH = EMPLEADOS_V1_BASE_PATH + "/login";
    public static final String DEPARTAMENTOS_V1_BASE_PATH = API_V1_BASE_PATH + "/departamentos";

    private ApiVersionConfig() {
    }
}