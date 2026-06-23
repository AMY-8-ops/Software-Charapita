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

import com.charapita.sistema.entity.Rol;
import com.charapita.sistema.service.IRolService;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final IRolService serviceRol;
    
    public RolController(IRolService serviceRol) {
        this.serviceRol = serviceRol;
    }

    @GetMapping
    public ResponseEntity<List<Rol>> buscarTodos() {
        return ResponseEntity.ok(serviceRol.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Integer id) {
        return serviceRol.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Rol rol) {
        try {
            Rol nuevoRol = serviceRol.guardar(rol);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRol);
        } catch (IllegalArgumentException e) {
            // Captura el error del servicio y devuelve un 400 Bad Request con tu mensaje personalizado
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody Rol rol) {
        if (rol.getIdrol() == null) {
            return ResponseEntity.badRequest().body("El ID es requerido para modificar");
        }
        try {
            Rol actualizada = serviceRol.actualizar(rol.getIdrol(), rol);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Integer id) {
        try {
            serviceRol.eliminar(id);
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