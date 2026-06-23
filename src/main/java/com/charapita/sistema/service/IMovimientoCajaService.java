package com.charapita.sistema.service;

import java.math.BigDecimal;
import java.util.List;

import com.charapita.sistema.dto.MovimientoCajaRequestDTO;
import com.charapita.sistema.dto.MovimientoCajaResponseDTO;

public interface IMovimientoCajaService {
    List<MovimientoCajaResponseDTO> listarHistorial();
    MovimientoCajaResponseDTO abrirCaja(MovimientoCajaRequestDTO dto);
    MovimientoCajaResponseDTO cerrarCaja(Integer idmovimiento, BigDecimal montofinal);
}