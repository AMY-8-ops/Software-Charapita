package com.charapita.sistema.service;

import java.util.List;

import com.charapita.sistema.dto.ClienteDTO;

public interface IClienteService {
    List<ClienteDTO> listarTodos();
    ClienteDTO buscarPorId(Integer id);
    ClienteDTO guardar(ClienteDTO dto);
    ClienteDTO actualizar(Integer id, ClienteDTO dto);
    void eliminar(Integer id);
}