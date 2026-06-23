package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.entity.Categoria;
import com.charapita.sistema.repository.CategoriaRepository;
import com.charapita.sistema.service.ICategoriaService;

@Service
public class CategoriaServiceImpl implements ICategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarTodos() {
        // Retornamos solo las categoriaes que tienen estado = true
        return categoriaRepository.findAll().stream()
                .filter(categoria -> categoria.getEstado() != null && categoria.getEstado()) 
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorId(Integer id) {
        return categoriaRepository.findById(id);
    }

    @Override
    @Transactional
    public Categoria guardar(Categoria categoria) {
        categoria.setEstado(true); // Por defecto activo al crear
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public Categoria actualizar(Integer id, Categoria categoriaRecibida) {
        // 1. Buscamos el existente
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("categoria no encontrada"));
        
        // 2. Comprobamos si el nombre viene en el JSON (no es null)
        if (categoriaRecibida.getNombre() != null && !categoriaRecibida.getNombre().isEmpty()) {
            existente.setNombre(categoriaRecibida.getNombre());
        }

        // 3. Comprobamos si el estado viene en el JSON (no es null)
        // Solo si viene algo, actualizamos. Si no, dejamos el que ya estaba.
        if (categoriaRecibida.getEstado() != null) {
            existente.setEstado(categoriaRecibida.getEstado());
        }
        
        // 4. Guardamos
        return categoriaRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        // Buscamos la categoria, si no existe, lanzamos error
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("categoria no encontrada"));
        
        // BORRADO LÓGICO: En lugar de borrar, cambiamos el estado a 0 (inactivo)
        existente.setEstado(false); // O existente.setEstado(0) dependiendo de cómo lo manejes
        
        // Guardamos los cambios
        categoriaRepository.save(existente);
    }
}