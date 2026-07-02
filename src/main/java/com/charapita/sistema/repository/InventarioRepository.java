package com.charapita.sistema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.charapita.sistema.entity.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
    Optional<Inventario> findByProducto_Idproducto(Integer idproducto);
    
    @Query("SELECT COUNT(i) FROM Inventario i WHERE i.stockactual <= i.stockminimo AND i.estado = true")
    long countStockCritico();
    
    @Query("SELECT i FROM Inventario i WHERE i.stockactual <= i.stockminimo AND i.estado = true ORDER BY i.stockactual ASC")
    List<Inventario> findStockCritico();
}