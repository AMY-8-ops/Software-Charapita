package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.dto.IngresoRequestDTO;
import com.charapita.sistema.entity.IngresoProduccion;

public interface IIngresoProduccionService {
    IngresoProduccion registrarIngreso(IngresoRequestDTO dto);
    IngresoProduccion actualizar(Integer id, IngresoRequestDTO dto);   
    List<IngresoProduccion> listarTodos();    
    Optional<IngresoProduccion> buscarPorId(Integer id);
    void eliminar(Integer id);
}