package com.charapita.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.Permiso;

public interface PermisoRepository extends JpaRepository<Permiso, Integer> {
}