package com.crudempleados.support;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class EmpleadoTestDataFactory {

    private EmpleadoTestDataFactory() {
    }

    public static Map<String, Object> createRequest(String seed) {
        String email = ("empleado." + seed + "@empresa.com").toLowerCase(Locale.ROOT);
        return createRequestWithCredentials(seed, email, "abc12345");
    }

    public static Map<String, Object> createRequestWithCredentials(String seed, String email, String password) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombre", "Nombre " + seed);
        payload.put("direccion", "Direccion " + seed);
        payload.put("telefono", "Telefono " + seed);
        payload.put("email", email);
        payload.put("password", password);
        return payload;
    }

    public static Map<String, Object> updateRequest(String seed) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombre", "Actualizado " + seed);
        payload.put("direccion", "Nueva direccion " + seed);
        payload.put("telefono", "Nuevo telefono " + seed);
        return payload;
    }

    public static Map<String, Object> loginRequest(String email, String password) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("password", password);
        return payload;
    }
}
