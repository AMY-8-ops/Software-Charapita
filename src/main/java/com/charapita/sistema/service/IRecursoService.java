package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.entity.Recurso;

public interface IRecursoService {
    List<Recurso> listarTodos();
    Optional<Recurso> buscarPorId(Integer id);
    Recurso guardar(Recurso recurso);
    Recurso actualizar(Integer id, Recurso recurso);
    void eliminar(Integer id);
}