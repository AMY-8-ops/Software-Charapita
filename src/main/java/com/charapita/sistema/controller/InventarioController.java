package com.charapita.sistema.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charapita.sistema.dto.InventarioResponseDTO;
import com.charapita.sistema.service.IInventarioService;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final IInventarioService serviceInventario;

    public InventarioController(IInventarioService serviceInventario) {
        this.serviceInventario = serviceInventario;
    }

    @GetMapping("/reporte")
    public ResponseEntity<List<InventarioResponseDTO>> obtenerReporte() {
        return ResponseEntity.ok(serviceInventario.obtenerReporteInventario());
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<InventarioResponseDTO>> obtenerAlertas() {
        return ResponseEntity.ok(serviceInventario.obtenerAlertasDeStock());
    }
}