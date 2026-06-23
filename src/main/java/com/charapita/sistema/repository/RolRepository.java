package com.charapita.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer> {
}