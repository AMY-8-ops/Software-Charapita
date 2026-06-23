package com.charapita.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.IngresoProduccion;

import java.util.List;
import java.util.Optional;

public interface IngresoProduccionRepository extends JpaRepository<IngresoProduccion, Integer> {
    List<IngresoProduccion> findByEstadoTrue();
    Optional<IngresoProduccion> findByIdingresoAndEstadoTrue(Integer idingreso);
}
