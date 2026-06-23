package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.entity.TipoCliente;
import com.charapita.sistema.repository.TipoClienteRepository;
import com.charapita.sistema.service.ITipoClienteService;

@Service
public class TipoClienteServiceImpl implements ITipoClienteService {

    private final TipoClienteRepository tipoclienteRepository;

    public TipoClienteServiceImpl(TipoClienteRepository tipoclienteRepository) {
        this.tipoclienteRepository = tipoclienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoCliente> listarTodos() {
        // Retornamos solo las tipoclientees que tienen estado = true
        return tipoclienteRepository.findAll().stream()
                .filter(tipocliente -> tipocliente.getEstado() != null && tipocliente.getEstado()) 
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoCliente> buscarPorId(Integer id) {
        return tipoclienteRepository.findById(id);
    }

    @Override
    @Transactional
    public TipoCliente guardar(TipoCliente TipoCliente) {
        TipoCliente.setEstado(true); // Por defecto activo al crear
        return tipoclienteRepository.save(TipoCliente);
    }

    @Override
    @Transactional
    public TipoCliente actualizar(Integer id, TipoCliente tipoclienteRecibida) {
        // 1. Buscamos el existente
        TipoCliente existente = tipoclienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("tipocliente no encontrada"));
        
        // 2. Comprobamos si el nombre viene en el JSON (no es null)
        if (tipoclienteRecibida.getNombre() != null && !tipoclienteRecibida.getNombre().isEmpty()) {
            existente.setNombre(tipoclienteRecibida.getNombre());
        }

        // 3. Comprobamos si el estado viene en el JSON (no es null)
        // Solo si viene algo, actualizamos. Si no, dejamos el que ya estaba.
        if (tipoclienteRecibida.getEstado() != null) {
            existente.setEstado(tipoclienteRecibida.getEstado());
        }
        
        // 4. Guardamos
        return tipoclienteRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        // Buscamos la Metodo de Pago, si no existe, lanzamos error
        TipoCliente existente = tipoclienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Metodo de Pago no encontrada"));
        
        // BORRADO LÓGICO: En lugar de borrar, cambiamos el estado a 0 (inactivo)
        existente.setEstado(false); // O existente.setEstado(0) dependiendo de cómo lo manejes
        
        // Guardamos los cambios
        tipoclienteRepository.save(existente);
    }
}