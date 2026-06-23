package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.entity.Categoria;

public interface ICategoriaService {
    List<Categoria> listarTodos();
    Optional<Categoria> buscarPorId(Integer id);
    Categoria guardar(Categoria categoria);
    Categoria actualizar(Integer id, Categoria categoria);
    void eliminar(Integer id);
}