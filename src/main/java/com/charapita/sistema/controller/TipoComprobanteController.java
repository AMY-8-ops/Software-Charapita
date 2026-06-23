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

import com.charapita.sistema.entity.TipoComprobante;
import com.charapita.sistema.service.ITipoComprobanteService;

@RestController
@RequestMapping("/api/tipocomprobantes")
public class TipoComprobanteController {

    private final ITipoComprobanteService serviceTipoComprobante;
    
    public TipoComprobanteController(ITipoComprobanteService serviceTipoComprobante) {
        this.serviceTipoComprobante = serviceTipoComprobante;
    }

    @GetMapping
    public ResponseEntity<List<TipoComprobante>> buscarTodos() {
        return ResponseEntity.ok(serviceTipoComprobante.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Integer id) {
        return serviceTipoComprobante.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<TipoComprobante> guardar(@RequestBody TipoComprobante tipocomprobante) {
        TipoComprobante nuevaTipoComprobante = serviceTipoComprobante.guardar(tipocomprobante);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTipoComprobante);
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody TipoComprobante tipocomprobante) {
        if (tipocomprobante.getIdtipo() == null) {
            return ResponseEntity.badRequest().body("El ID es requerido para modificar");
        }
        try {
            TipoComprobante actualizada = serviceTipoComprobante.actualizar(tipocomprobante.getIdtipo(), tipocomprobante);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Integer id) {
        try {
            serviceTipoComprobante.eliminar(id);
            return ResponseEntity.ok("Categoría eliminada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    /*para los bobos que quieren eliminar algo sin el id xd */
    @DeleteMapping
    public ResponseEntity<String> eliminarSinId() {
        return ResponseEntity.badRequest().body("Error: El ID es obligatorio en la URL para poder eliminar un registro.");
    }
}