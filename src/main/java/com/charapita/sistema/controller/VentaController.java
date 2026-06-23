package com.charapita.sistema.controller;

import java.util.List;
import java.util.Optional;

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

import com.charapita.sistema.dto.VentaRequestDTO;
import com.charapita.sistema.dto.VentaResponseDTO;
import com.charapita.sistema.service.IVentaService;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final IVentaService serviceVenta;

    public VentaController(IVentaService serviceVenta) {
        this.serviceVenta = serviceVenta;
    }

    // LISTAR TODAS (GET http://localhost:8080/api/ventas)
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(serviceVenta.listarTodas());
    }

    // BUSCAR POR ID CON DETALLES (GET http://localhost:8080/api/ventas/{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable("id") Integer id) {
        Optional<VentaResponseDTO> ventaOpt = serviceVenta.buscarPorId(id);
        if (ventaOpt.isPresent()) {
            return ResponseEntity.ok(ventaOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Venta no encontrada");
    }

    // registrar Venta (POST http://localhost:8080/api/ventas)
    @PostMapping
    public ResponseEntity<?> registrarVenta(@RequestBody VentaRequestDTO dto) {
        try {
            VentaResponseDTO nuevaVenta = serviceVenta.registrarVenta(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error en los datos de la venta: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error inesperado al procesar la venta");
        }
    }

    // ANULAR CON DELETE (DELETE http://localhost:8080/api/ventas/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarVenta(@PathVariable("id") Integer id) {
        try {
            serviceVenta.anularVenta(id);
            return ResponseEntity.ok("Venta anulada exitosamente y stock devuelto.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error inesperado al anular la venta: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<String> eliminarSinId() {
        return ResponseEntity.badRequest()
                .body("Error: El ID es obligatorio en la URL para poder eliminar un registro.");
    }

    // ANULAR CON PUT EXPLÍCITO (PUT http://localhost:8080/api/ventas/{id}/anular)
    @PutMapping("/{id}/anular")
    public ResponseEntity<?> anularVenta(@PathVariable("id") Integer id) {
        try {
            serviceVenta.anularVenta(id);
            return ResponseEntity.ok("Venta anulada exitosamente y stock devuelto.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error inesperado al anular la venta: " + e.getMessage());
        }
    }

    @PutMapping("/anular")
    public ResponseEntity<String> anularSinId() {
        return ResponseEntity.badRequest()
                .body("Error: El ID de la venta es obligatorio en la URL para poder anularla.");
    }
}