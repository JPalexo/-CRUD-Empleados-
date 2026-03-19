package com.crudempleados.service;

import com.crudempleados.domain.ClaveEmpleadoCodec;
import com.crudempleados.dto.EmpleadoResponse;
import com.crudempleados.model.Empleado;
import com.crudempleados.model.EmpleadoId;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoMapper {

    private final ClaveEmpleadoCodec claveCodec;

    public EmpleadoMapper(ClaveEmpleadoCodec claveCodec) {
        this.claveCodec = claveCodec;
    }

    public EmpleadoResponse toResponse(Empleado empleado) {
        EmpleadoId id = new EmpleadoId(empleado.getClavePrefijo(), empleado.getClaveNumero());
        return new EmpleadoResponse(
            claveCodec.format(id),
            empleado.getNombre(),
            empleado.getDireccion(),
            empleado.getTelefono()
        );
    }
}
