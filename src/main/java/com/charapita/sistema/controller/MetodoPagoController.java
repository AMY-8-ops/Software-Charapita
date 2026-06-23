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

import com.charapita.sistema.entity.MetodoPago;
import com.charapita.sistema.service.IMetodoPagoService;

@RestController
@RequestMapping("/api/metodopagos")
public class MetodoPagoController {

    private final IMetodoPagoService serviceMetodoPago;
    
    public MetodoPagoController(IMetodoPagoService serviceMetodoPago) {
        this.serviceMetodoPago = serviceMetodoPago;
    }

    @GetMapping
    public ResponseEntity<List<MetodoPago>> buscarTodos() {
        return ResponseEntity.ok(serviceMetodoPago.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Integer id) {
        return serviceMetodoPago.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<MetodoPago> guardar(@RequestBody MetodoPago metodopago) {
        MetodoPago nuevaMetodoPago = serviceMetodoPago.guardar(metodopago);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMetodoPago);
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody MetodoPago metodopago) {
        if (metodopago.getIdmetodo() == null) {
            return ResponseEntity.badRequest().body("El ID es requerido para modificar");
        }
        try {
            MetodoPago actualizada = serviceMetodoPago.actualizar(metodopago.getIdmetodo(), metodopago);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Integer id) {
        try {
            serviceMetodoPago.eliminar(id);
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