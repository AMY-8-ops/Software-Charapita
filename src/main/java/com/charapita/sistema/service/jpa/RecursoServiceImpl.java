package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.entity.Recurso;
import com.charapita.sistema.repository.RecursoRepository;
import com.charapita.sistema.service.IRecursoService;

@Service
public class RecursoServiceImpl implements IRecursoService {

    private final RecursoRepository recursoRepository;

    public RecursoServiceImpl(RecursoRepository recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Recurso> listarTodos() {
        // Retornamos solo las recursoes que tienen estado = true
        return recursoRepository.findAll().stream()
                .filter(recurso -> recurso.getEstado() != null && recurso.getEstado()) 
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Recurso> buscarPorId(Integer id) {
        return recursoRepository.findById(id);
    }

    @Override
    @Transactional
    public Recurso guardar(Recurso Recurso) {
        Recurso.setEstado(true); // Por defecto activo al crear
        return recursoRepository.save(Recurso);
    }

    @Override
    @Transactional
    public Recurso actualizar(Integer id, Recurso recursoRecibida) {
        // 1. Buscamos el existente
        Recurso existente = recursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("recurso no encontrada"));
        
        // 2. Comprobamos si el nombre viene en el JSON (no es null)
        if (recursoRecibida.getNombre() != null && !recursoRecibida.getNombre().isEmpty()) {
            existente.setNombre(recursoRecibida.getNombre());
        }

        // 3. Comprobamos si el estado viene en el JSON (no es null)
        // Solo si viene algo, actualizamos. Si no, dejamos el que ya estaba.
        if (recursoRecibida.getEstado() != null) {
            existente.setEstado(recursoRecibida.getEstado());
        }
        
        // 4. Guardamos
        return recursoRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        // Buscamos la Metodo de Pago, si no existe, lanzamos error
        Recurso existente = recursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Metodo de Pago no encontrada"));
        
        // BORRADO LÓGICO: En lugar de borrar, cambiamos el estado a 0 (inactivo)
        existente.setEstado(false); // O existente.setEstado(0) dependiendo de cómo lo manejes
        
        // Guardamos los cambios
        recursoRepository.save(existente);
    }
}