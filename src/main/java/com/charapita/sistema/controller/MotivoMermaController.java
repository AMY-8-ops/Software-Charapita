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

import com.charapita.sistema.entity.MotivoMerma;
import com.charapita.sistema.service.IMotivoMermaService;

@RestController
@RequestMapping("/api/motivomermas")
public class MotivoMermaController {

    private final IMotivoMermaService serviceMotivoMerma;
    
    public MotivoMermaController(IMotivoMermaService serviceMotivoMerma) {
        this.serviceMotivoMerma = serviceMotivoMerma;
    }

    @GetMapping
    public ResponseEntity<List<MotivoMerma>> buscarTodos() {
        return ResponseEntity.ok(serviceMotivoMerma.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Integer id) {
        return serviceMotivoMerma.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<MotivoMerma> guardar(@RequestBody MotivoMerma motivomerma) {
        MotivoMerma nuevaMotivoMerma = serviceMotivoMerma.guardar(motivomerma);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMotivoMerma);
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody MotivoMerma motivomerma) {
        if (motivomerma.getIdmotivo() == null) {
            return ResponseEntity.badRequest().body("El ID es requerido para modificar");
        }
        try {
            MotivoMerma actualizada = serviceMotivoMerma.actualizar(motivomerma.getIdmotivo(), motivomerma);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Integer id) {
        try {
            serviceMotivoMerma.eliminar(id);
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