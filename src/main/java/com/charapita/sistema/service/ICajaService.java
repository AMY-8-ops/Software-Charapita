package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.entity.Caja;

public interface ICajaService {
    List<Caja> listarTodos();
    Optional<Caja> buscarPorId(Integer id);
    Caja guardar(Caja caja);
    Caja actualizar(Integer id, Caja caja);
    void eliminar(Integer id);
}