package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.entity.Presentacion;
import com.charapita.sistema.repository.PresentacionRepository;
import com.charapita.sistema.service.IPresentacionService;

@Service
public class PresentacionServiceImpl implements IPresentacionService {

    private final PresentacionRepository presentacionRepository;

    public PresentacionServiceImpl(PresentacionRepository presentacionRepository) {
        this.presentacionRepository = presentacionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Presentacion> listarTodos() {
        // Retornamos solo las presentaciones que tienen estado = true
        return presentacionRepository.findAll().stream()
                .filter(presentacion -> presentacion.getEstado() != null && presentacion.getEstado()) 
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Presentacion> buscarPorId(Integer id) {
        return presentacionRepository.findById(id);
    }

    @Override
    @Transactional
    public Presentacion guardar(Presentacion Presentacion) {
        Presentacion.setEstado(true); // Por defecto activo al crear
        return presentacionRepository.save(Presentacion);
    }

    @Override
    @Transactional
    public Presentacion actualizar(Integer id, Presentacion presentacionRecibida) {
        // 1. Buscamos el existente
        Presentacion existente = presentacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("presentacion no encontrada"));
        
        // 2. Comprobamos si el nombre viene en el JSON (no es null)
        if (presentacionRecibida.getDescripcion() != null && !presentacionRecibida.getDescripcion().isEmpty()) {
            existente.setDescripcion(presentacionRecibida.getDescripcion());
        }

        // 3. Comprobamos si el estado viene en el JSON (no es null)
        // Solo si viene algo, actualizamos. Si no, dejamos el que ya estaba.
        if (presentacionRecibida.getEstado() != null) {
            existente.setEstado(presentacionRecibida.getEstado());
        }
        
        // 4. Guardamos
        return presentacionRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        // Buscamos la Metodo de Pago, si no existe, lanzamos error
        Presentacion existente = presentacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Metodo de Pago no encontrada"));
        
        // BORRADO LÓGICO: En lugar de borrar, cambiamos el estado a 0 (inactivo)
        existente.setEstado(false); // O existente.setEstado(0) dependiendo de cómo lo manejes
        
        // Guardamos los cambios
        presentacionRepository.save(existente);
    }
}