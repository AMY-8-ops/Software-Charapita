package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.entity.TipoComprobante;

public interface ITipoComprobanteService {
    List<TipoComprobante> listarTodos();
    Optional<TipoComprobante> buscarPorId(Integer id);
    TipoComprobante guardar(TipoComprobante tipocomprobante);
    TipoComprobante actualizar(Integer id, TipoComprobante tipocomprobante);
    void eliminar(Integer id);
}