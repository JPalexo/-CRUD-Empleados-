package com.crudempleados.domain;

import com.crudempleados.domain.exception.InvalidClaveFormatException;
import com.crudempleados.model.DepartamentoId;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class ClaveDepartamentoCodec {

    public static final String PREFIX = "DEP-";
    public static final String CLAVE_REGEX = "^DEP-[1-9][0-9]*$";
    private static final Pattern CLAVE_PATTERN = Pattern.compile(CLAVE_REGEX);

    public DepartamentoId parse(String clave) {
        if (clave == null || !CLAVE_PATTERN.matcher(clave).matches()) {
            throw new InvalidClaveFormatException("Field 'clave' must match DEP-{numero} without leading zeros.");
        }
        long numero = Long.parseLong(clave.substring(PREFIX.length()));
        return new DepartamentoId(PREFIX, numero);
    }

    public String format(DepartamentoId id) {
        if (id == null || id.getClaveNumero() == null || id.getClaveNumero() <= 0 || !PREFIX.equals(id.getClavePrefijo())) {
            throw new InvalidClaveFormatException("Cannot format an invalid department key.");
        }
        return PREFIX + id.getClaveNumero();
    }
}