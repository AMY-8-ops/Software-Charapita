package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.entity.MotivoMerma;

public interface IMotivoMermaService {
    List<MotivoMerma> listarTodos();
    Optional<MotivoMerma> buscarPorId(Integer id);
    MotivoMerma guardar(MotivoMerma motivomerma);
    MotivoMerma actualizar(Integer id, MotivoMerma motivomerma);
    void eliminar(Integer id);
}