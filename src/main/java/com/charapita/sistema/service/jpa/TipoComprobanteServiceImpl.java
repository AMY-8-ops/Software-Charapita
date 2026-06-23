package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.entity.TipoComprobante;
import com.charapita.sistema.repository.TipoComprobanteRepository;
import com.charapita.sistema.service.ITipoComprobanteService;

@Service
public class TipoComprobanteServiceImpl implements ITipoComprobanteService {

    private final TipoComprobanteRepository tipocomprobanteRepository;

    public TipoComprobanteServiceImpl(TipoComprobanteRepository tipocomprobanteRepository) {
        this.tipocomprobanteRepository = tipocomprobanteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoComprobante> listarTodos() {
        // Retornamos solo las tipocomprobantees que tienen estado = true
        return tipocomprobanteRepository.findAll().stream()
                .filter(tipocomprobante -> tipocomprobante.getEstado() != null && tipocomprobante.getEstado()) 
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoComprobante> buscarPorId(Integer id) {
        return tipocomprobanteRepository.findById(id);
    }

    @Override
    @Transactional
    public TipoComprobante guardar(TipoComprobante TipoComprobante) {
        TipoComprobante.setEstado(true); // Por defecto activo al crear
        return tipocomprobanteRepository.save(TipoComprobante);
    }

    @Override
    @Transactional
    public TipoComprobante actualizar(Integer id, TipoComprobante tipocomprobanteRecibida) {
        // 1. Buscamos el existente
        TipoComprobante existente = tipocomprobanteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("tipocomprobante no encontrada"));
        
        // 2. Comprobamos si el nombre viene en el JSON (no es null)
        if (tipocomprobanteRecibida.getNombre() != null && !tipocomprobanteRecibida.getNombre().isEmpty()) {
            existente.setNombre(tipocomprobanteRecibida.getNombre());
        }

        // 3. Comprobamos si el estado viene en el JSON (no es null)
        // Solo si viene algo, actualizamos. Si no, dejamos el que ya estaba.
        if (tipocomprobanteRecibida.getEstado() != null) {
            existente.setEstado(tipocomprobanteRecibida.getEstado());
        }
        
        // 4. Guardamos
        return tipocomprobanteRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        // Buscamos la Metodo de Pago, si no existe, lanzamos error
        TipoComprobante existente = tipocomprobanteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de Comprobante no encontrado"));
        
        // BORRADO LÓGICO: En lugar de borrar, cambiamos el estado a 0 (inactivo)
        existente.setEstado(false); // O existente.setEstado(0) dependiendo de cómo lo manejes
        
        // Guardamos los cambios
        tipocomprobanteRepository.save(existente);
    }
}