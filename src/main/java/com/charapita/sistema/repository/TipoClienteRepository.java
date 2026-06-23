package com.charapita.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.TipoCliente;

public interface TipoClienteRepository extends JpaRepository<TipoCliente, Integer> {
}