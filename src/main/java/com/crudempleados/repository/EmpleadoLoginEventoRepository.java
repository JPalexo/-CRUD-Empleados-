package com.crudempleados.repository;

import java.util.List;
import com.crudempleados.model.EmpleadoLoginEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoLoginEventoRepository extends JpaRepository<EmpleadoLoginEvento, Long> {

    List<EmpleadoLoginEvento> findAllByEmailNormalizadoInputOrderByIdAsc(String emailNormalizadoInput);
}