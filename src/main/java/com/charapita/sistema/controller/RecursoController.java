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

import com.charapita.sistema.entity.Recurso;
import com.charapita.sistema.service.IRecursoService;

@RestController
@RequestMapping("/api/recursos")
public class RecursoController {

    private final IRecursoService serviceRecurso;
    
    public RecursoController(IRecursoService serviceRecurso) {
        this.serviceRecurso = serviceRecurso;
    }

    @GetMapping
    public ResponseEntity<List<Recurso>> buscarTodos() {
        return ResponseEntity.ok(serviceRecurso.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Integer id) {
        return serviceRecurso.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Recurso> guardar(@RequestBody Recurso recurso) {
        Recurso nuevaRecurso = serviceRecurso.guardar(recurso);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaRecurso);
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody Recurso recurso) {
        if (recurso.getIdrecurso() == null) {
            return ResponseEntity.badRequest().body("El ID es requerido para modificar");
        }
        try {
            Recurso actualizada = serviceRecurso.actualizar(recurso.getIdrecurso(), recurso);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Integer id) {
        try {
            serviceRecurso.eliminar(id);
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