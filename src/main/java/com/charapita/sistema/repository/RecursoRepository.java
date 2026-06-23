package com.charapita.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.Recurso;

public interface RecursoRepository extends JpaRepository<Recurso, Integer> {
}