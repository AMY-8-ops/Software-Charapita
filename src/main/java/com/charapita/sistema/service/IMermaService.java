package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.dto.MermaRequestDTO;
import com.charapita.sistema.dto.MermaResponseDTO;

public interface IMermaService {
    List<MermaResponseDTO> listarHistorial();
    void registrarMerma(MermaRequestDTO dto);
    Optional<MermaResponseDTO> buscarPorId(Integer id);
    void eliminar(Integer id);
}