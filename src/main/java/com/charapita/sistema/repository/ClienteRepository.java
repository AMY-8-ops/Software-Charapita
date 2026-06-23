package com.charapita.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}