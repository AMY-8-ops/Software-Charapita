package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.entity.MotivoMerma;
import com.charapita.sistema.repository.MotivoMermaRepository;
import com.charapita.sistema.service.IMotivoMermaService;

@Service
public class MotivoMermaServiceImpl implements IMotivoMermaService {

    private final MotivoMermaRepository motivomermaRepository;

    public MotivoMermaServiceImpl(MotivoMermaRepository motivomermaRepository) {
        this.motivomermaRepository = motivomermaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MotivoMerma> listarTodos() {
        // Retornamos solo las motivomermaes que tienen estado = true
        return motivomermaRepository.findAll().stream()
                .filter(motivomerma -> motivomerma.getEstado() != null && motivomerma.getEstado()) 
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MotivoMerma> buscarPorId(Integer id) {
        return motivomermaRepository.findById(id);
    }

    @Override
    @Transactional
    public MotivoMerma guardar(MotivoMerma MotivoMerma) {
        MotivoMerma.setEstado(true); // Por defecto activo al crear
        return motivomermaRepository.save(MotivoMerma);
    }

    @Override
    @Transactional
    public MotivoMerma actualizar(Integer id, MotivoMerma motivomermaRecibida) {
        // 1. Buscamos el existente
        MotivoMerma existente = motivomermaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("motivomerma no encontrada"));
        
        // 2. Comprobamos si el nombre viene en el JSON (no es null)
        if (motivomermaRecibida.getDescripcion() != null && !motivomermaRecibida.getDescripcion().isEmpty()) {
            existente.setDescripcion(motivomermaRecibida.getDescripcion());
        }

        // 3. Comprobamos si el estado viene en el JSON (no es null)
        // Solo si viene algo, actualizamos. Si no, dejamos el que ya estaba.
        if (motivomermaRecibida.getEstado() != null) {
            existente.setEstado(motivomermaRecibida.getEstado());
        }
        
        // 4. Guardamos
        return motivomermaRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        // Buscamos la Metodo de Pago, si no existe, lanzamos error
        MotivoMerma existente = motivomermaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Metodo de Pago no encontrada"));
        
        // BORRADO LÓGICO: En lugar de borrar, cambiamos el estado a 0 (inactivo)
        existente.setEstado(false); // O existente.setEstado(0) dependiendo de cómo lo manejes
        
        // Guardamos los cambios
        motivomermaRepository.save(existente);
    }
}