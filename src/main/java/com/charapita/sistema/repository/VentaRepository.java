package com.charapita.sistema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.Usuario;
import com.charapita.sistema.entity.Venta;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByUsuario(Usuario usuario);
    Optional<Venta> findByNroPedido(String nroPedido);
    List<Venta> findByEstadoTrue();
}