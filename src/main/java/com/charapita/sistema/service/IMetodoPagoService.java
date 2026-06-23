package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.entity.MetodoPago;

public interface IMetodoPagoService {
    List<MetodoPago> listarTodos();
    Optional<MetodoPago> buscarPorId(Integer id);
    MetodoPago guardar(MetodoPago metodopago);
    MetodoPago actualizar(Integer id, MetodoPago metodopago);
    void eliminar(Integer id);
}