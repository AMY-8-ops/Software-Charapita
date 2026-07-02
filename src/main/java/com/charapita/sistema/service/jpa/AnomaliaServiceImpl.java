package com.charapita.sistema.service.jpa;

import java.time.LocalDateTime;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.charapita.sistema.dto.AnomaliaDTO;
import com.charapita.sistema.dto.AnomaliaResponseDTO;
import com.charapita.sistema.entity.Anomalia;
import com.charapita.sistema.entity.MovimientoCaja;
import com.charapita.sistema.repository.AnomaliaRepository;
import com.charapita.sistema.repository.MovimientoCajaRepository;
import com.charapita.sistema.service.IAnomaliaService;

@Service
public class AnomaliaServiceImpl implements IAnomaliaService {

    private final AnomaliaRepository anomaliaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final RestTemplate restTemplate;

    public AnomaliaServiceImpl(AnomaliaRepository anomaliaRepository, MovimientoCajaRepository movimientoCajaRepository) {
        this.anomaliaRepository = anomaliaRepository;
        this.movimientoCajaRepository = movimientoCajaRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    @Transactional
    public void evaluarMovimiento(MovimientoCaja movimiento) {
        if (movimiento.getMontofinal() == null) {
            return;
        }

        try {
            // 1. Obtener historial de montos finales
            List<MovimientoCaja> historicos = movimientoCajaRepository.findAll().stream()
                    .filter(m -> m.getFhCierre() != null && m.getMontofinal() != null)
                    .toList();

            List<Double> historialMontos = new ArrayList<>();
            for (MovimientoCaja m : historicos) {
                historialMontos.add(m.getMontofinal().doubleValue());
            }

            // Si hay pocos datos, mandamos algo basico para evitar errores del modelo IsolationForest
            if (historialMontos.size() < 5) {
                historialMontos.addAll(List.of(100.0, 200.0, 300.0, 400.0, 500.0));
            }

            // 2. Preparar el payload
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("historial_montos", historialMontos);
            requestBody.put("monto_actual", movimiento.getMontofinal().doubleValue());

            String url = "http://localhost:8000/detect_anomaly";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 3. Consumir la API
            ResponseEntity<AnomaliaResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, AnomaliaResponseDTO.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                AnomaliaResponseDTO aiResponse = response.getBody();
                
                // Si es anomalía, guardamos en BD
                if (Boolean.TRUE.equals(aiResponse.getIsAnomaly())) {
                    Anomalia anomalia = new Anomalia();
                    anomalia.setFechaDeteccion(LocalDateTime.now());
                    anomalia.setMontoEvaluado(movimiento.getMontofinal());
                    anomalia.setScore(aiResponse.getScore());
                    anomalia.setLeido(false);
                    anomalia.setMovimientoCaja(movimiento);
                    anomaliaRepository.save(anomalia);
                }
            }
        } catch (Exception e) {
            // Manejo de errores silencioso para no interrumpir el cierre de caja normal si falla la IA
            System.err.println("Error evaluando anomalía: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnomaliaDTO> obtenerNoLeidas() {
        return anomaliaRepository.findByLeidoFalseOrderByFechaDeteccionDesc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void marcarComoLeidas(List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            List<Anomalia> anomalias = anomaliaRepository.findAllById(ids);
            for (Anomalia a : anomalias) {
                a.setLeido(true);
            }
            anomaliaRepository.saveAll(anomalias);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnomaliaDTO> getAnomaliasByDateRange(String fechaInicio, String fechaFin) {
        List<Anomalia> anomalias;
        if (fechaInicio != null && !fechaInicio.isEmpty() && fechaFin != null && !fechaFin.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(fechaInicio + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(fechaFin + "T23:59:59");
            anomalias = anomaliaRepository.findByFechaDeteccionBetweenOrderByFechaDeteccionDesc(start, end);
        } else {
            anomalias = anomaliaRepository.findAllByOrderByFechaDeteccionDesc();
        }
        return anomalias.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    private AnomaliaDTO convertirADTO(Anomalia a) {
        AnomaliaDTO dto = new AnomaliaDTO();
        dto.setIdanomalia(a.getIdanomalia());
        dto.setFechaDeteccion(a.getFechaDeteccion());
        dto.setMontoEvaluado(a.getMontoEvaluado());
        dto.setScore(a.getScore());
        dto.setLeido(a.getLeido());
        if (a.getMovimientoCaja() != null) {
            if (a.getMovimientoCaja().getCaja() != null) {
                dto.setNombreCaja(a.getMovimientoCaja().getCaja().getNombre());
            }
            if (a.getMovimientoCaja().getUsuario() != null) {
                dto.setNombreCajero(a.getMovimientoCaja().getUsuario().getNombre() + " " + a.getMovimientoCaja().getUsuario().getApellido());
            }
        }
        return dto;
    }
}
