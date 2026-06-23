package com.charapita.sistema.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
    Optional<Inventario> findByProducto_Idproducto(Integer idproducto);
}