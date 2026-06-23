package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.entity.Accion;

public interface IAccionService {
    List<Accion> listarTodos();
    Optional<Accion> buscarPorId(Integer id);
    Accion guardar(Accion accion);
    Accion actualizar(Integer id, Accion accion);
    void eliminar(Integer id);
}