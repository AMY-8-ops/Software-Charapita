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

import com.charapita.sistema.entity.TipoCliente;
import com.charapita.sistema.service.ITipoClienteService;

@RestController
@RequestMapping("/api/tipoclientes")
public class TipoClienteController {

    private final ITipoClienteService serviceTipoCliente;
    
    public TipoClienteController(ITipoClienteService serviceTipoCliente) {
        this.serviceTipoCliente = serviceTipoCliente;
    }

    @GetMapping
    public ResponseEntity<List<TipoCliente>> buscarTodos() {
        return ResponseEntity.ok(serviceTipoCliente.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Integer id) {
        return serviceTipoCliente.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<TipoCliente> guardar(@RequestBody TipoCliente tipocliente) {
        TipoCliente nuevaTipoCliente = serviceTipoCliente.guardar(tipocliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTipoCliente);
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody TipoCliente tipocliente) {
        if (tipocliente.getIdtipocliente() == null) {
            return ResponseEntity.badRequest().body("El ID es requerido para modificar");
        }
        try {
            TipoCliente actualizada = serviceTipoCliente.actualizar(tipocliente.getIdtipocliente(), tipocliente);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Integer id) {
        try {
            serviceTipoCliente.eliminar(id);
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