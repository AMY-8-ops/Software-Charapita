package com.charapita.sistema.service;

import java.util.List;

import com.charapita.sistema.dto.AnomaliaDTO;
import com.charapita.sistema.entity.MovimientoCaja;

public interface IAnomaliaService {
    void evaluarMovimiento(MovimientoCaja movimiento);
    List<AnomaliaDTO> obtenerNoLeidas();
    void marcarComoLeidas(List<Integer> ids);
    
    List<AnomaliaDTO> getAnomaliasByDateRange(String fechaInicio, String fechaFin);
}
