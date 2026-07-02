package com.charapita.sistema.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.charapita.sistema.dto.AnomaliaDTO;
import com.charapita.sistema.service.IAnomaliaService;

@RestController
@RequestMapping("/api/anomalias")
public class AnomaliaRestController {

    private final IAnomaliaService anomaliaService;

    public AnomaliaRestController(IAnomaliaService anomaliaService) {
        this.anomaliaService = anomaliaService;
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<List<AnomaliaDTO>> getNoLeidas() {
        return ResponseEntity.ok(anomaliaService.obtenerNoLeidas());
    }

    @PostMapping("/marcar-leidas")
    public ResponseEntity<?> marcarComoLeidas(@RequestBody Map<String, List<Integer>> payload) {
        List<Integer> ids = payload.get("ids");
        if (ids != null && !ids.isEmpty()) {
            anomaliaService.marcarComoLeidas(ids);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<AnomaliaDTO>> getAnomaliasFiltered(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        return ResponseEntity.ok(anomaliaService.getAnomaliasByDateRange(fechaInicio, fechaFin));
    }
}
