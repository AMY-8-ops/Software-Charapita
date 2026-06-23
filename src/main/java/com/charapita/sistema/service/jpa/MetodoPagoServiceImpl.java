package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.entity.MetodoPago;
import com.charapita.sistema.repository.MetodoPagoRepository;
import com.charapita.sistema.service.IMetodoPagoService;

@Service
public class MetodoPagoServiceImpl implements IMetodoPagoService {

    private final MetodoPagoRepository metodopagoRepository;

    public MetodoPagoServiceImpl(MetodoPagoRepository metodopagoRepository) {
        this.metodopagoRepository = metodopagoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPago> listarTodos() {
        // Retornamos solo las metodopagoes que tienen estado = true
        return metodopagoRepository.findAll().stream()
                .filter(metodopago -> metodopago.getEstado() != null && metodopago.getEstado()) 
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MetodoPago> buscarPorId(Integer id) {
        return metodopagoRepository.findById(id);
    }

    @Override
    @Transactional
    public MetodoPago guardar(MetodoPago MetodoPago) {
        MetodoPago.setEstado(true); // Por defecto activo al crear
        return metodopagoRepository.save(MetodoPago);
    }

    @Override
    @Transactional
    public MetodoPago actualizar(Integer id, MetodoPago metodopagoRecibida) {
        // 1. Buscamos el existente
        MetodoPago existente = metodopagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("metodopago no encontrada"));
        
        // 2. Comprobamos si el nombre viene en el JSON (no es null)
        if (metodopagoRecibida.getNombre() != null && !metodopagoRecibida.getNombre().isEmpty()) {
            existente.setNombre(metodopagoRecibida.getNombre());
        }

        // 3. Comprobamos si el estado viene en el JSON (no es null)
        // Solo si viene algo, actualizamos. Si no, dejamos el que ya estaba.
        if (metodopagoRecibida.getEstado() != null) {
            existente.setEstado(metodopagoRecibida.getEstado());
        }
        
        // 4. Guardamos
        return metodopagoRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        // Buscamos la Metodo de Pago, si no existe, lanzamos error
        MetodoPago existente = metodopagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Metodo de Pago no encontrada"));
        
        // BORRADO LÓGICO: En lugar de borrar, cambiamos el estado a 0 (inactivo)
        existente.setEstado(false); // O existente.setEstado(0) dependiendo de cómo lo manejes
        
        // Guardamos los cambios
        metodopagoRepository.save(existente);
    }
}