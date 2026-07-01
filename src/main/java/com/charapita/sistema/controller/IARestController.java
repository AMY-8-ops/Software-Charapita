package com.charapita.sistema.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.charapita.sistema.dto.AnomaliaResponseDTO;
import com.charapita.sistema.dto.RecomendacionResponseDTO;
import com.charapita.sistema.entity.DetalleVenta;
import com.charapita.sistema.entity.Producto;
import com.charapita.sistema.entity.Venta;
import com.charapita.sistema.repository.DetalleVentaRepository;
import com.charapita.sistema.repository.ProductoRepository;
import com.charapita.sistema.repository.VentaRepository;

@RestController
@RequestMapping("/api/ia")
public class IARestController {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final RestTemplate restTemplate;

    public IARestController(VentaRepository ventaRepository, DetalleVentaRepository detalleVentaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/recommend")
    public ResponseEntity<?> getRecomendaciones(@RequestParam List<Integer> carrito) {
        try {
            // 1. Obtener historial de ventas activas
            List<Venta> ventasActivas = ventaRepository.findAll().stream()
                    .filter(v -> Boolean.TRUE.equals(v.getEstado()))
                    .toList();

            List<List<Integer>> historial = new ArrayList<>();
            for (Venta v : ventasActivas) {
                List<DetalleVenta> detalles = detalleVentaRepository.findByIdIdventa(v.getIdventa());
                List<Integer> productosIds = detalles.stream()
                        .map(d -> d.getProducto().getIdproducto())
                        .collect(Collectors.toList());
                if (!productosIds.isEmpty()) {
                    historial.add(productosIds);
                }
            }

            // 2. Preparar request al microservicio Python
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("historial", historial);
            requestBody.put("carrito", carrito);
            requestBody.put("top_k", 3);

            String url = "http://localhost:8000/recommend";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<RecomendacionResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, RecomendacionResponseDTO.class);

            List<Producto> productosRecomendados = new ArrayList<>();
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Integer> recomendadosIds = response.getBody().getRecomendaciones();
                if (recomendadosIds != null) {
                    for (Integer id : recomendadosIds) {
                        productoRepository.findById(id).ifPresent(productosRecomendados::add);
                    }
                }
            }

            return ResponseEntity.ok(productosRecomendados);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/anomaly")
    public ResponseEntity<?> checkAnomaly(@RequestParam Double monto) {
        try {
            // 1. Obtener montos históricos (ventas sumadas)
            List<Venta> ventasActivas = ventaRepository.findAll().stream()
                    .filter(v -> Boolean.TRUE.equals(v.getEstado()))
                    .toList();

            List<Double> historialMontos = new ArrayList<>();
            for (Venta v : ventasActivas) {
                List<DetalleVenta> detalles = detalleVentaRepository.findByIdIdventa(v.getIdventa());
                BigDecimal totalVenta = detalles.stream()
                        .map(d -> d.getImporte() != null ? d.getImporte() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (totalVenta.compareTo(BigDecimal.ZERO) > 0) {
                    historialMontos.add(totalVenta.doubleValue());
                }
            }

            // Si hay pocos datos, mandamos algo básico para que no falle IsolationForest
            if (historialMontos.size() < 5) {
                historialMontos.addAll(List.of(10.0, 20.0, 30.0, 40.0, 50.0));
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("historial_montos", historialMontos);
            requestBody.put("monto_actual", monto);

            String url = "http://localhost:8000/detect_anomaly";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<AnomaliaResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, AnomaliaResponseDTO.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(500).body(Map.of("error", "Error del microservicio AI"));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
