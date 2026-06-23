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

import com.charapita.sistema.entity.Accion;
import com.charapita.sistema.service.IAccionService;

@RestController
@RequestMapping("/api/acciones")
public class AccionController {

    private final IAccionService serviceAccion;
    
    public AccionController(IAccionService serviceAccion) {
        this.serviceAccion = serviceAccion;
    }

    @GetMapping
    public ResponseEntity<List<Accion>> buscarTodos() {
        return ResponseEntity.ok(serviceAccion.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Integer id) {
        return serviceAccion.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Accion> guardar(@RequestBody Accion accion) {
        Accion nuevaAccion = serviceAccion.guardar(accion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAccion);
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody Accion accion) {
        if (accion.getIdaccion() == null) {
            return ResponseEntity.badRequest().body("El ID es requerido para modificar");
        }
        try {
            Accion actualizada = serviceAccion.actualizar(accion.getIdaccion(), accion);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Integer id) {
        try {
            serviceAccion.eliminar(id);
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