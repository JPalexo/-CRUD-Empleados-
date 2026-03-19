package com.crudempleados.service;

import java.util.Optional;
import com.crudempleados.domain.ClaveEmpleadoCodec;
import com.crudempleados.domain.EmailNormalizer;
import com.crudempleados.domain.exception.InvalidEmployeeCredentialsException;
import com.crudempleados.dto.EmpleadoLoginRequest;
import com.crudempleados.dto.EmpleadoLoginResponse;
import com.crudempleados.model.Empleado;
import com.crudempleados.model.EmpleadoCredencial;
import com.crudempleados.model.EmpleadoId;
import com.crudempleados.repository.EmpleadoCredencialRepository;
import com.crudempleados.repository.EmpleadoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpleadoLoginService {

    private final EmpleadoCredencialRepository empleadoCredencialRepository;
    private final EmpleadoRepository empleadoRepository;
    private final EmailNormalizer emailNormalizer;
    private final PasswordEncoder passwordEncoder;
    private final EmpleadoLoginEventService empleadoLoginEventService;
    private final ClaveEmpleadoCodec claveEmpleadoCodec;

    public EmpleadoLoginService(
        EmpleadoCredencialRepository empleadoCredencialRepository,
        EmpleadoRepository empleadoRepository,
        EmailNormalizer emailNormalizer,
        PasswordEncoder passwordEncoder,
        EmpleadoLoginEventService empleadoLoginEventService,
        ClaveEmpleadoCodec claveEmpleadoCodec
    ) {
        this.empleadoCredencialRepository = empleadoCredencialRepository;
        this.empleadoRepository = empleadoRepository;
        this.emailNormalizer = emailNormalizer;
        this.passwordEncoder = passwordEncoder;
        this.empleadoLoginEventService = empleadoLoginEventService;
        this.claveEmpleadoCodec = claveEmpleadoCodec;
    }

    @Transactional
    public EmpleadoLoginResponse login(EmpleadoLoginRequest request) {
        String emailNormalizado = emailNormalizer.normalize(request.getEmail());
        Optional<EmpleadoCredencial> credencialOpt = empleadoCredencialRepository.findByEmailNormalizado(emailNormalizado);

        if (credencialOpt.isEmpty()) {
            empleadoLoginEventService.recordFailure(emailNormalizado, "INVALID_CREDENTIALS");
            throw new InvalidEmployeeCredentialsException();
        }

        EmpleadoCredencial credencial = credencialOpt.get();
        EmpleadoId empleadoId = new EmpleadoId(credencial.getClavePrefijo(), credencial.getClaveNumero());

        if (!Boolean.TRUE.equals(credencial.getHabilitada())) {
            empleadoLoginEventService.recordFailure(emailNormalizado, empleadoId, "CREDENTIAL_NOT_ENABLED");
            throw new InvalidEmployeeCredentialsException();
        }

        Optional<Empleado> empleadoOpt = empleadoRepository.findById(empleadoId);
        if (empleadoOpt.isEmpty()) {
            empleadoLoginEventService.recordFailure(emailNormalizado, empleadoId, "EMPLOYEE_NOT_FOUND");
            throw new InvalidEmployeeCredentialsException();
        }

        if (!passwordEncoder.matches(request.getPassword(), credencial.getPasswordHash())) {
            empleadoLoginEventService.recordFailure(emailNormalizado, empleadoId, "INVALID_CREDENTIALS");
            throw new InvalidEmployeeCredentialsException();
        }

        Empleado empleado = empleadoOpt.get();
        empleadoLoginEventService.recordSuccess(emailNormalizado, empleadoId);

        EmpleadoLoginResponse.EmpleadoLoginIdentity identity = new EmpleadoLoginResponse.EmpleadoLoginIdentity(
            claveEmpleadoCodec.format(empleadoId),
            empleado.getNombre(),
            emailNormalizado
        );

        return new EmpleadoLoginResponse(true, identity);
    }
}