package com.charapita.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}