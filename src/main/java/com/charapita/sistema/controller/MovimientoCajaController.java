package com.charapita.sistema.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charapita.sistema.dto.MovimientoCajaRequestDTO;
import com.charapita.sistema.dto.MovimientoCajaResponseDTO;
import com.charapita.sistema.service.IMovimientoCajaService;

@RestController
@RequestMapping("/api/movimientoscaja")
public class MovimientoCajaController {

    private final IMovimientoCajaService serviceMovimiento;

    public MovimientoCajaController(IMovimientoCajaService serviceMovimiento) {
        this.serviceMovimiento = serviceMovimiento;
    }

    @GetMapping
    public ResponseEntity<List<MovimientoCajaResponseDTO>> listarHistorial() {
        return ResponseEntity.ok(serviceMovimiento.listarHistorial());
    }

    @PostMapping("/abrir")
    public ResponseEntity<?> abrirCaja(@RequestBody MovimientoCajaRequestDTO dto) {
        try {
            MovimientoCajaResponseDTO result = serviceMovimiento.abrirCaja(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/cerrar/{id}")
    public ResponseEntity<?> cerrarCaja(@PathVariable("id") Integer idmovimiento, @RequestBody Map<String, BigDecimal> payload) {
        try {
            // Extraemos el monto final del JSON recibido: {"montofinal": 1500.50}
            BigDecimal montofinal = payload.get("montofinal");
            if (montofinal == null) {
                return ResponseEntity.badRequest().body("Debe enviar el montofinal");
            }
            
            MovimientoCajaResponseDTO result = serviceMovimiento.cerrarCaja(idmovimiento, montofinal);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}