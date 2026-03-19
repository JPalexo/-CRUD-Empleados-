package com.crudempleados.repository;

import java.util.Optional;
import com.crudempleados.model.EmpleadoCredencial;
import com.crudempleados.model.EmpleadoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoCredencialRepository extends JpaRepository<EmpleadoCredencial, EmpleadoId> {

    Optional<EmpleadoCredencial> findByEmailNormalizado(String emailNormalizado);

    boolean existsByEmailNormalizado(String emailNormalizado);
}