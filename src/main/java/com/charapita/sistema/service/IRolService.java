package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.entity.Rol;

public interface IRolService {
    List<Rol> listarTodos();
    Optional<Rol> buscarPorId(Integer id);
    Rol guardar(Rol rol);
    Rol actualizar(Integer id, Rol rol);
    void eliminar(Integer id);
}