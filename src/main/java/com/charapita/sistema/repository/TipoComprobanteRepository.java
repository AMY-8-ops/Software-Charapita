package com.charapita.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.TipoComprobante;

public interface TipoComprobanteRepository extends JpaRepository<TipoComprobante, Integer> {
}