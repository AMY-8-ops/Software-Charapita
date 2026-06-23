package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.entity.TipoCliente;

public interface ITipoClienteService {
    List<TipoCliente> listarTodos();
    Optional<TipoCliente> buscarPorId(Integer id);
    TipoCliente guardar(TipoCliente tipocliente);
    TipoCliente actualizar(Integer id, TipoCliente tipocliente);
    void eliminar(Integer id);
}