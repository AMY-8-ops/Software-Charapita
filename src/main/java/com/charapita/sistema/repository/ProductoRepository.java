package com.charapita.sistema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByEstadoTrue();
}