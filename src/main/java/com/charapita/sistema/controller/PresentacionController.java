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

import com.charapita.sistema.entity.Presentacion;
import com.charapita.sistema.service.IPresentacionService;

@RestController
@RequestMapping("/api/presentaciones")
public class PresentacionController {

    private final IPresentacionService servicePresentacion;
    
    public PresentacionController(IPresentacionService servicePresentacion) {
        this.servicePresentacion = servicePresentacion;
    }

    @GetMapping
    public ResponseEntity<List<Presentacion>> buscarTodos() {
        return ResponseEntity.ok(servicePresentacion.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Integer id) {
        return servicePresentacion.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Presentacion> guardar(@RequestBody Presentacion presentacion) {
        Presentacion nuevaPresentacion = servicePresentacion.guardar(presentacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPresentacion);
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody Presentacion presentacion) {
        if (presentacion.getIdpresentacion() == null) {
            return ResponseEntity.badRequest().body("El ID es requerido para modificar");
        }
        try {
            Presentacion actualizada = servicePresentacion.actualizar(presentacion.getIdpresentacion(), presentacion);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Integer id) {
        try {
            servicePresentacion.eliminar(id);
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