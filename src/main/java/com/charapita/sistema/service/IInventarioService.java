package com.charapita.sistema.service;

import java.util.List;

import com.charapita.sistema.dto.InventarioResponseDTO;

public interface IInventarioService {
    List<InventarioResponseDTO> obtenerReporteInventario();
    List<InventarioResponseDTO> obtenerAlertasDeStock();
}