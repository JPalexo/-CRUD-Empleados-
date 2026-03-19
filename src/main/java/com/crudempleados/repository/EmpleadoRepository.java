package com.crudempleados.repository;

import com.crudempleados.model.Empleado;
import com.crudempleados.model.EmpleadoId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmpleadoRepository extends JpaRepository<Empleado, EmpleadoId> {

	Page<Empleado> findAllByOrderByClaveNumeroAsc(Pageable pageable);

	@Query(value = "SELECT nextval('empleados_clave_numero_seq')", nativeQuery = true)
	Long nextClaveNumero();
}
