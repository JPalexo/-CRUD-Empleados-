package com.crudempleados.service;

import com.crudempleados.domain.EmailNormalizer;
import com.crudempleados.domain.PasswordPolicy;
import com.crudempleados.domain.exception.DuplicateEmployeeEmailException;
import com.crudempleados.model.Empleado;
import com.crudempleados.model.EmpleadoCredencial;
import com.crudempleados.repository.EmpleadoCredencialRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoCredentialService {

    private final EmpleadoCredencialRepository empleadoCredencialRepository;
    private final EmailNormalizer emailNormalizer;
    private final PasswordPolicy passwordPolicy;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoCredentialService(
        EmpleadoCredencialRepository empleadoCredencialRepository,
        EmailNormalizer emailNormalizer,
        PasswordPolicy passwordPolicy,
        PasswordEncoder passwordEncoder
    ) {
        this.empleadoCredencialRepository = empleadoCredencialRepository;
        this.emailNormalizer = emailNormalizer;
        this.passwordPolicy = passwordPolicy;
        this.passwordEncoder = passwordEncoder;
    }

    public EmpleadoCredencial createCredentialFor(Empleado empleado, String rawEmail, String rawPassword) {
        String emailNormalizado = emailNormalizer.normalize(rawEmail);
        passwordPolicy.validateOrThrow(rawPassword);

        if (empleadoCredencialRepository.existsByEmailNormalizado(emailNormalizado)) {
            throw new DuplicateEmployeeEmailException(emailNormalizado);
        }

        EmpleadoCredencial credencial = new EmpleadoCredencial();
        credencial.setClavePrefijo(empleado.getClavePrefijo());
        credencial.setClaveNumero(empleado.getClaveNumero());
        credencial.setEmailNormalizado(emailNormalizado);
        credencial.setPasswordHash(passwordEncoder.encode(rawPassword));
        credencial.setHabilitada(true);

        return empleadoCredencialRepository.save(credencial);
    }
}