package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.entity.Accion;
import com.charapita.sistema.repository.AccionRepository;
import com.charapita.sistema.service.IAccionService;

@Service
public class AccionServiceImpl implements IAccionService {

    private final AccionRepository accionRepository;

    public AccionServiceImpl(AccionRepository accionRepository) {
        this.accionRepository = accionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Accion> listarTodos() {
        // Retornamos solo las acciones que tienen estado = true
        return accionRepository.findAll().stream()
                .filter(accion -> accion.getEstado() != null && accion.getEstado()) 
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Accion> buscarPorId(Integer id) {
        return accionRepository.findById(id);
    }

    @Override
    @Transactional
    public Accion guardar(Accion Accion) {
        Accion.setEstado(true);
        return accionRepository.save(Accion);
    }

    @Override
    @Transactional
    public Accion actualizar(Integer id, Accion accionRecibida) {
        // 1. Buscamos el existente
        Accion existente = accionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Accion no encontrada"));
        
        // 2. Comprobamos si el nombre viene en el JSON (no es null)
        if (accionRecibida.getNombre() != null && !accionRecibida.getNombre().isEmpty()) {
            existente.setNombre(accionRecibida.getNombre());
        }

        // 3. Comprobamos si el estado viene en el JSON (no es null)
        // Solo si viene algo, actualizamos. Si no, dejamos el que ya estaba.
        if (accionRecibida.getEstado() != null) {
            existente.setEstado(accionRecibida.getEstado());
        }
        
        // 4. Guardamos
        return accionRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        // Buscamos la accion, si no existe, lanzamos error
        Accion existente = accionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Accion no encontrada"));
        
        // BORRADO LÓGICO: En lugar de borrar, cambiamos el estado a 0 (inactivo)
        existente.setEstado(false); // O existente.setEstado(0) dependiendo de cómo lo manejes
        
        // Guardamos los cambios
        accionRepository.save(existente);
    }
}