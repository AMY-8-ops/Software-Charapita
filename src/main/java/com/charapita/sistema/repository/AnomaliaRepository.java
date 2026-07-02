package com.charapita.sistema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.charapita.sistema.entity.Anomalia;

@Repository
public interface AnomaliaRepository extends JpaRepository<Anomalia, Integer> {
    
    List<Anomalia> findByLeidoFalseOrderByFechaDeteccionDesc();
    
    List<Anomalia> findAllByOrderByFechaDeteccionDesc();
    
    List<Anomalia> findByFechaDeteccionBetweenOrderByFechaDeteccionDesc(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
