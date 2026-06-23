package com.charapita.sistema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.DetalleVenta;
import com.charapita.sistema.entity.DetalleVentaId;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, DetalleVentaId> {
    List<DetalleVenta> findByIdIdventa(Integer idventa);
    
}