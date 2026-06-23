package com.charapita.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.HistorialAcceso;

public interface HistorialAccesoRepository extends JpaRepository<HistorialAcceso, Integer> {
}