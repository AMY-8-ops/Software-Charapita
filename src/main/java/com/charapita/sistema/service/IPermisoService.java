package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.entity.Permiso;

public interface IPermisoService {
    List<Permiso> listarTodos();
    Optional<Permiso> buscarPorId(Integer id);
    Permiso guardar(Permiso permiso);
    Permiso actualizar(Integer id, Permiso permiso);
    void eliminar(Integer id); // Borrado lógico
}