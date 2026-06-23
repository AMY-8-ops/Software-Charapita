package com.charapita.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.Accion;

public interface AccionRepository extends JpaRepository<Accion, Integer> {
}