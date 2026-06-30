package com.charapita.sistema.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.charapita.sistema.dto.PrediccionResponseDTO;
import com.charapita.sistema.dto.VentaDiariaDTO;
import com.charapita.sistema.entity.DetalleVenta;
import com.charapita.sistema.entity.Venta;
import com.charapita.sistema.repository.DetalleVentaRepository;
import com.charapita.sistema.repository.VentaRepository;

@RestController
@RequestMapping("/api/reportes")
public class ReporteRestController {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final RestTemplate restTemplate;

    public ReporteRestController(VentaRepository ventaRepository, DetalleVentaRepository detalleVentaRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/prediccion")
    public ResponseEntity<?> getPrediccionIngresos() {
        try {
            // 1. Obtener el historial de ventas reales agrupadas por fecha
            List<Venta> ventasActivas = ventaRepository.findAll().stream()
                    .filter(v -> Boolean.TRUE.equals(v.getEstado()) && v.getFecha() != null)
                    .toList();

            Map<LocalDate, BigDecimal> ingresosPorDia = new TreeMap<>();
            
            for (Venta v : ventasActivas) {
                LocalDate fecha = v.getFecha().toLocalDate();
                List<DetalleVenta> detalles = detalleVentaRepository.findByIdIdventa(v.getIdventa());
                
                BigDecimal totalVenta = detalles.stream()
                        .map(d -> d.getImporte() != null ? d.getImporte() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                ingresosPorDia.put(fecha, ingresosPorDia.getOrDefault(fecha, BigDecimal.ZERO).add(totalVenta));
            }

            // Si no hay datos, devolver lista vacía para evitar fallos en frontend
            if (ingresosPorDia.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "historico", new ArrayList<>(),
                        "prediccion", new ArrayList<>()
                ));
            }

            List<VentaDiariaDTO> historial = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Map.Entry<LocalDate, BigDecimal> entry : ingresosPorDia.entrySet()) {
                historial.add(new VentaDiariaDTO(entry.getKey().format(formatter), entry.getValue().doubleValue()));
            }

            // 2. Enviar historial al microservicio Python
            String url = "http://localhost:8000/predict";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<VentaDiariaDTO>> request = new HttpEntity<>(historial, headers);

            List<PrediccionResponseDTO> prediccion = new ArrayList<>();
            try {
                ResponseEntity<List<PrediccionResponseDTO>> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        new ParameterizedTypeReference<List<PrediccionResponseDTO>>() {}
                );
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    prediccion = response.getBody();
                }
            } catch (Exception e) {
                // El microservicio no está disponible o devolvió un error
                System.err.println("Error al contactar microservicio Python: " + e.getMessage());
            }

            // 3. Devolver datos consolidados al frontend
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("historico", historial);
            resultado.put("prediccion", prediccion);

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
