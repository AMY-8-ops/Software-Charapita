package com.charapita.sistema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charapita.sistema.entity.DetalleIngreso;
import com.charapita.sistema.entity.DetalleIngresoId;

public interface DetalleIngresoRepository extends JpaRepository<DetalleIngreso, DetalleIngresoId> {
    
    // Fíjate que al final dice "idingreso" todo en minúscula
    List<DetalleIngreso> findByIdIdingreso(Integer idingreso); 
    
}