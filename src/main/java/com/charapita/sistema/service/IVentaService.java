package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.dto.VentaRequestDTO;
import com.charapita.sistema.dto.VentaResponseDTO;

public interface IVentaService {
    VentaResponseDTO registrarVenta(VentaRequestDTO dto);
    List<VentaResponseDTO> listarTodas();
    Optional<VentaResponseDTO> buscarPorId(Integer id);
    void anularVenta(Integer id);
}