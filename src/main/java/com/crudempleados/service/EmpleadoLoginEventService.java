package com.crudempleados.service;

import com.crudempleados.model.EmpleadoId;
import com.crudempleados.model.EmpleadoLoginEvento;
import com.crudempleados.repository.EmpleadoLoginEventoRepository;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoLoginEventService {

    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_FAILURE = "FAILURE";

    private final EmpleadoLoginEventoRepository empleadoLoginEventoRepository;

    public EmpleadoLoginEventService(EmpleadoLoginEventoRepository empleadoLoginEventoRepository) {
        this.empleadoLoginEventoRepository = empleadoLoginEventoRepository;
    }

    public void recordSuccess(String emailNormalizadoInput, EmpleadoId empleadoId) {
        persist(emailNormalizadoInput, empleadoId, RESULT_SUCCESS, "AUTHENTICATED");
    }

    public void recordFailure(String emailNormalizadoInput, String motivo) {
        persist(emailNormalizadoInput, null, RESULT_FAILURE, motivo);
    }

    public void recordFailure(String emailNormalizadoInput, EmpleadoId empleadoId, String motivo) {
        persist(emailNormalizadoInput, empleadoId, RESULT_FAILURE, motivo);
    }

    private void persist(String emailNormalizadoInput, EmpleadoId empleadoId, String resultado, String motivo) {
        EmpleadoLoginEvento evento = new EmpleadoLoginEvento();
        evento.setEmailNormalizadoInput(emailNormalizadoInput);
        if (empleadoId != null) {
            evento.setClavePrefijo(empleadoId.getClavePrefijo());
            evento.setClaveNumero(empleadoId.getClaveNumero());
        }
        evento.setResultado(resultado);
        evento.setMotivo(motivo);
        empleadoLoginEventoRepository.save(evento);
    }
}