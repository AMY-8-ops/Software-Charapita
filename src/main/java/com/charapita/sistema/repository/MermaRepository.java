package com.charapita.sistema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.Merma;

public interface MermaRepository extends JpaRepository<Merma, Integer> {
    List<Merma> findTop5ByEstadoTrueOrderByFechahoraDesc();
}