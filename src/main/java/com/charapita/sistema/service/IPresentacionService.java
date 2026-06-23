package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.entity.Presentacion;

public interface IPresentacionService {
    List<Presentacion> listarTodos();
    Optional<Presentacion> buscarPorId(Integer id);
    Presentacion guardar(Presentacion presentacion);
    Presentacion actualizar(Integer id, Presentacion presentacion);
    void eliminar(Integer id);
}