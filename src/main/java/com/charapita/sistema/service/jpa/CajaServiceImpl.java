package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.entity.Caja;
import com.charapita.sistema.repository.CajaRepository;
import com.charapita.sistema.service.ICajaService;

@Service
public class CajaServiceImpl implements ICajaService {

    private final CajaRepository cajaRepository;

    public CajaServiceImpl(CajaRepository cajaRepository) {
        this.cajaRepository = cajaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Caja> listarTodos() {
        // Retornamos solo las que tienen estado = true (o 1)
        return cajaRepository.findAll().stream()
                .filter(caja -> caja.getEstado()) 
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Caja> buscarPorId(Integer id) {
        return cajaRepository.findById(id);
    }

    @Override
    @Transactional
    public Caja guardar(Caja Caja) {
        Caja.setEstado(true); // Por defecto activo al crear
        return cajaRepository.save(Caja);
    }

    @Override
    @Transactional
    public Caja actualizar(Integer id, Caja cajaRecibida) {
        // 1. Buscamos el existente
        Caja existente = cajaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Caja no encontrada"));
        
        // 2. Comprobamos si el nombre viene en el JSON (no es null)
        if (cajaRecibida.getNombre() != null && !cajaRecibida.getNombre().isEmpty()) {
            existente.setNombre(cajaRecibida.getNombre());
        }

        // 3. Comprobamos si el estado viene en el JSON (no es null)
        // Solo si viene algo, actualizamos. Si no, dejamos el que ya estaba.
        if (cajaRecibida.getEstado() != null) {
            existente.setEstado(cajaRecibida.getEstado());
        }
        
        // 4. Guardamos
        return cajaRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        // Buscamos la caja, si no existe, lanzamos error
        Caja existente = cajaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Caja no encontrada"));
        
        // BORRADO LÓGICO: En lugar de borrar, cambiamos el estado a 0 (inactivo)
        existente.setEstado(false); // O existente.setEstado(0) dependiendo de cómo lo manejes
        
        // Guardamos los cambios
        cajaRepository.save(existente);
    }
}