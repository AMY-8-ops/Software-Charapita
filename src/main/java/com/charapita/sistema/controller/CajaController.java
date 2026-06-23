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

import com.charapita.sistema.entity.Caja;
import com.charapita.sistema.service.ICajaService;

@RestController
@RequestMapping("/api/cajas")
public class CajaController {

    private final ICajaService serviceCaja;
    
    public CajaController(ICajaService serviceCaja) {
        this.serviceCaja = serviceCaja;
    }

    @GetMapping
    public ResponseEntity<List<Caja>> buscarTodos() {
        return ResponseEntity.ok(serviceCaja.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Integer id) {
        return serviceCaja.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Caja> guardar(@RequestBody Caja caja) {
        Caja nuevaCaja = serviceCaja.guardar(caja);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCaja);
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody Caja caja) {
        if (caja.getIdcaja() == null) {
            return ResponseEntity.badRequest().body("El ID es requerido para modificar");
        }
        try {
            Caja actualizada = serviceCaja.actualizar(caja.getIdcaja(), caja);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Integer id) {
        try {
            serviceCaja.eliminar(id);
            return ResponseEntity.ok("Caja eliminada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping
    public ResponseEntity<String> eliminarSinId() {
        return ResponseEntity.badRequest().body("Error: El ID es obligatorio en la URL para poder eliminar un registro.");
    }
}