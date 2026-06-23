package com.charapita.sistema.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charapita.sistema.entity.Categoria;
import com.charapita.sistema.service.ICategoriaService;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final ICategoriaService serviceCategoria;
    
    public CategoriaController(ICategoriaService serviceCategoria) {
        this.serviceCategoria = serviceCategoria;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> buscarTodos() {
        return ResponseEntity.ok(serviceCategoria.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Integer id) {
        return serviceCategoria.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Categoria> guardar(@RequestBody Categoria categoria) {
        Categoria nuevaCategoria = serviceCategoria.guardar(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody Categoria categoria) {
        if (categoria.getIdcategoria() == null) {
            return ResponseEntity.badRequest().body("El ID es requerido para modificar");
        }
        try {
            Categoria actualizada = serviceCategoria.actualizar(categoria.getIdcategoria(), categoria);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Integer id) {
        try {
            serviceCategoria.eliminar(id);
            return ResponseEntity.ok("Categoría eliminada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping
    public ResponseEntity<String> eliminarSinId() {
        return ResponseEntity.badRequest().body("Error: El ID es obligatorio en la URL para poder eliminar un registro.");
    }
}