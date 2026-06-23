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

import com.charapita.sistema.entity.Permiso;
import com.charapita.sistema.service.IPermisoService;

@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    private final IPermisoService servicePermiso;

    public PermisoController(IPermisoService servicePermiso) {
        this.servicePermiso = servicePermiso;
    }

    @GetMapping
    public ResponseEntity<List<Permiso>> buscarTodos() {
        return ResponseEntity.ok(servicePermiso.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Integer id) {
        return servicePermiso.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Permiso permiso) {
        try {
            Permiso nuevoPermiso = servicePermiso.guardar(permiso);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPermiso);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody Permiso permiso) {
        if (permiso.getIdpermiso() == null) {
            return ResponseEntity.badRequest().body("El ID es requerido para modificar");
        }
        try {
            Permiso actualizado = servicePermiso.actualizar(permiso.getIdpermiso(), permiso);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Integer id) {
        try {
            servicePermiso.eliminar(id);
            return ResponseEntity.ok("Permiso eliminado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping
    public ResponseEntity<String> eliminarSinId() {
        return ResponseEntity.badRequest().body("Error: El ID es obligatorio en la URL para poder eliminar un registro.");
    }
}