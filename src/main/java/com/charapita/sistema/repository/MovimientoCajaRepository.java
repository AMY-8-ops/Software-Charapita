package com.charapita.sistema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.MovimientoCaja;

public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Integer> {
    List<MovimientoCaja> findByCajaIdcaja(Integer idcaja);
}