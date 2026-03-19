package com.crudempleados.domain;

import com.crudempleados.domain.exception.InvalidClaveFormatException;
import com.crudempleados.model.EmpleadoId;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class ClaveEmpleadoCodec {

    public static final String PREFIX = "EMP-";
    public static final String CLAVE_REGEX = "^EMP-[1-9][0-9]*$";
    private static final Pattern CLAVE_PATTERN = Pattern.compile(CLAVE_REGEX);

    public EmpleadoId parse(String clave) {
        if (clave == null || !CLAVE_PATTERN.matcher(clave).matches()) {
            throw new InvalidClaveFormatException("Field 'clave' must match EMP-{numero} without leading zeros.");
        }
        long numero = Long.parseLong(clave.substring(PREFIX.length()));
        return new EmpleadoId(PREFIX, numero);
    }

    public String format(EmpleadoId id) {
        if (id == null || id.getClaveNumero() == null || id.getClaveNumero() <= 0 || !PREFIX.equals(id.getClavePrefijo())) {
            throw new InvalidClaveFormatException("Cannot format an invalid employee key.");
        }
        return PREFIX + id.getClaveNumero();
    }
}
