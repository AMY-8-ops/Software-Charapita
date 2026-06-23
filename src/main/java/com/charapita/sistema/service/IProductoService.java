package com.charapita.sistema.service;

import java.util.List;

import com.charapita.sistema.dto.ProductoResponseDTO;
import com.charapita.sistema.entity.Producto;

public interface IProductoService {
    List<ProductoResponseDTO> listarTodosActivos();
    Producto guardar(Producto producto);
    void cambiarEstado(Integer id, boolean estado);
    Producto actualizar(Integer id, Producto producto);
}