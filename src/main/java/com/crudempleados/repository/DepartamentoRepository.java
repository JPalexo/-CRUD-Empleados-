package com.crudempleados.repository;

import com.crudempleados.model.Departamento;
import com.crudempleados.model.DepartamentoId;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;

public interface DepartamentoRepository extends JpaRepository<Departamento, DepartamentoId> {

    Page<Departamento> findAllByOrderByClaveNumeroAsc(Pageable pageable);

    Optional<Departamento> findByNombreNormalizado(String nombreNormalizado);

    boolean existsByNombreNormalizado(String nombreNormalizado);

    boolean existsByNombreNormalizadoAndClaveNumeroNot(String nombreNormalizado, Long claveNumero);

    @Query(value = "SELECT nextval('departamentos_clave_numero_seq')", nativeQuery = true)
    Long nextClaveNumero();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Departamento d where d.clavePrefijo = :prefijo and d.claveNumero = :numero")
    Optional<Departamento> findByIdForUpdate(String prefijo, Long numero);
}