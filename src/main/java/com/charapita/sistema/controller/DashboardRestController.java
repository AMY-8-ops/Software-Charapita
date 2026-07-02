package com.charapita.sistema.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charapita.sistema.entity.DetalleVenta;
import com.charapita.sistema.entity.Inventario;
import com.charapita.sistema.entity.Merma;
import com.charapita.sistema.entity.MovimientoCaja;
import com.charapita.sistema.entity.Venta;
import com.charapita.sistema.repository.DetalleVentaRepository;
import com.charapita.sistema.repository.InventarioRepository;
import com.charapita.sistema.repository.MermaRepository;
import com.charapita.sistema.repository.MovimientoCajaRepository;
import com.charapita.sistema.repository.VentaRepository;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final InventarioRepository inventarioRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final MermaRepository mermaRepository;

    public DashboardRestController(
            VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            InventarioRepository inventarioRepository,
            MovimientoCajaRepository movimientoCajaRepository,
            MermaRepository mermaRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.inventarioRepository = inventarioRepository;
        this.movimientoCajaRepository = movimientoCajaRepository;
        this.mermaRepository = mermaRepository;
    }

    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> getKpis() {
        Map<String, Object> result = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // --- 1. VENTAS DEL DÍA ---
        LocalDateTime startToday = today.atStartOfDay();
        LocalDateTime endToday = today.plusDays(1).atStartOfDay();
        List<Venta> ventasHoy = ventaRepository.findByFechaBetweenAndEstadoTrue(startToday, endToday);
        BigDecimal ventasDia = calcularTotalVentas(ventasHoy);

        // --- 2. VENTAS DE AYER (para calcular % variación) ---
        LocalDateTime startYesterday = yesterday.atStartOfDay();
        LocalDateTime endYesterday = today.atStartOfDay();
        List<Venta> ventasAyer = ventaRepository.findByFechaBetweenAndEstadoTrue(startYesterday, endYesterday);
        BigDecimal ventasAyerTotal = calcularTotalVentas(ventasAyer);

        double variacionVentas = 0.0;
        if (ventasAyerTotal.compareTo(BigDecimal.ZERO) > 0) {
            variacionVentas = ventasDia.subtract(ventasAyerTotal)
                    .divide(ventasAyerTotal, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // --- 3. SALDO ACTUAL EN CAJA ---
        // Tomamos la suma de los montos finales de los movimientos cerrados hoy,
        // o si hay uno abierto, tomamos el monto inicial del movimiento activo.
        List<MovimientoCaja> todosMovimientos = movimientoCajaRepository.findAll();
        
        BigDecimal saldoCaja = BigDecimal.ZERO;
        // Primero buscamos si hay un movimiento abierto (sin cierre)
        boolean cajaAbierta = false;
        for (MovimientoCaja mc : todosMovimientos) {
            if (mc.getFhCierre() == null && mc.getMontoinicial() != null) {
                saldoCaja = saldoCaja.add(mc.getMontoinicial());
                cajaAbierta = true;
            }
        }
        // Si no hay cajas abiertas, tomamos el último cierre del día de hoy
        if (!cajaAbierta) {
            for (MovimientoCaja mc : todosMovimientos) {
                if (mc.getFhCierre() != null 
                        && mc.getFhCierre().toLocalDate().equals(today) 
                        && mc.getMontofinal() != null) {
                    saldoCaja = saldoCaja.add(mc.getMontofinal());
                }
            }
            // Si tampoco hay cierres hoy, tomamos el último cierre disponible
            if (saldoCaja.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal ultimoCierre = todosMovimientos.stream()
                        .filter(mc -> mc.getMontofinal() != null && mc.getFhCierre() != null)
                        .map(MovimientoCaja::getMontofinal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                saldoCaja = ultimoCierre;
            }
        }

        // --- 4. ALERTAS STOCK CRÍTICO ---
        long alertasStock = inventarioRepository.countStockCritico();

        // --- CONSTRUIR RESPUESTA ---
        result.put("ventasDia", ventasDia.setScale(2, RoundingMode.HALF_UP));
        result.put("variacionVentas", Math.round(variacionVentas * 10.0) / 10.0);
        result.put("saldoCaja", saldoCaja.setScale(2, RoundingMode.HALF_UP));
        result.put("alertasStock", alertasStock);

        return ResponseEntity.ok(result);
    }

    private BigDecimal calcularTotalVentas(List<Venta> ventas) {
        BigDecimal total = BigDecimal.ZERO;
        for (Venta v : ventas) {
            List<DetalleVenta> detalles = detalleVentaRepository.findByIdIdventa(v.getIdventa());
            for (DetalleVenta d : detalles) {
                if (d.getImporte() != null) {
                    total = total.add(d.getImporte());
                }
            }
        }
        return total;
    }

    // ----------------------------------------------------------------
    // VENTAS DE LA SEMANA (últimos 7 días)
    // ----------------------------------------------------------------
    @GetMapping("/ventas-semana")
    public ResponseEntity<Map<String, Object>> getVentasSemana() {
        LocalDate today = LocalDate.now();
        List<String> labels = new ArrayList<>();
        List<Double> valores = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate dia = today.minusDays(i);
            LocalDateTime inicio = dia.atStartOfDay();
            LocalDateTime fin = dia.plusDays(1).atStartOfDay();

            List<Venta> ventasDia = ventaRepository.findByFechaBetweenAndEstadoTrue(inicio, fin);
            BigDecimal totalDia = calcularTotalVentas(ventasDia);

            // Label: nombre corto del día (Lun, Mar...) + número
            String label = dia.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, new Locale("es", "PE"))
                    + " " + dia.getDayOfMonth();
            labels.add(label);
            valores.add(totalDia.setScale(2, RoundingMode.HALF_UP).doubleValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("valores", valores);
        return ResponseEntity.ok(result);
    }

    // ----------------------------------------------------------------
    // VENTAS POR CATEGORÍA (totales históricos activos)
    // ----------------------------------------------------------------
    @GetMapping("/ventas-categoria")
    public ResponseEntity<Map<String, Object>> getVentasCategoria() {
        // Traemos todos los detalles de ventas activas
        List<Venta> todasVentas = ventaRepository.findByEstadoTrue();

        // Acumular por categoría usando LinkedHashMap para mantener orden
        Map<String, BigDecimal> porCategoria = new LinkedHashMap<>();

        for (Venta v : todasVentas) {
            List<DetalleVenta> detalles = detalleVentaRepository.findByIdIdventa(v.getIdventa());
            for (DetalleVenta d : detalles) {
                if (d.getImporte() == null || d.getProducto() == null) continue;
                String cat = "Sin Categoría";
                if (d.getProducto().getCategoria() != null
                        && d.getProducto().getCategoria().getNombre() != null) {
                    cat = d.getProducto().getCategoria().getNombre();
                }
                porCategoria.merge(cat, d.getImporte(), BigDecimal::add);
            }
        }

        // Calcular total
        BigDecimal total = porCategoria.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        List<String> labels = new ArrayList<>(porCategoria.keySet());
        List<Double> valores = new ArrayList<>();
        for (BigDecimal v : porCategoria.values()) {
            valores.add(v.setScale(2, RoundingMode.HALF_UP).doubleValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("valores", valores);
        result.put("total", total.setScale(2, RoundingMode.HALF_UP));
        return ResponseEntity.ok(result);
    }

    // ----------------------------------------------------------------
    // TOP 5 PRODUCTOS CON MENOS STOCK
    // ----------------------------------------------------------------
    @GetMapping("/stock-critico")
    public ResponseEntity<List<Map<String, Object>>> getStockCritico() {
        List<Inventario> criticos = inventarioRepository.findStockCritico();
        // Limitamos a 5
        List<Map<String, Object>> result = new ArrayList<>();
        int limit = Math.min(5, criticos.size());
        for (int i = 0; i < limit; i++) {
            Inventario inv = criticos.get(i);
            Map<String, Object> row = new HashMap<>();
            row.put("nombre", inv.getProducto() != null ? inv.getProducto().getNombre() : "-");
            row.put("imagenUrl", inv.getProducto() != null ? inv.getProducto().getImagenUrl() : null);
            row.put("stockActual", inv.getStockactual());
            row.put("stockMinimo", inv.getStockminimo());
            // Estado: CRITICO si stockactual == 0, BAJO si <= minimo
            String estado;
            String estadoClass;
            if (inv.getStockactual() != null && inv.getStockactual() == 0) {
                estado = "Agotado";
                estadoClass = "critico";
            } else if (inv.getStockactual() != null && inv.getStockminimo() != null
                    && inv.getStockactual() <= inv.getStockminimo()) {
                estado = "Crítico";
                estadoClass = "critico";
            } else {
                estado = "Bajo";
                estadoClass = "bajo";
            }
            row.put("estado", estado);
            row.put("estadoClass", estadoClass);
            result.add(row);
        }
        return ResponseEntity.ok(result);
    }

    // ----------------------------------------------------------------
    // RESUMEN DE MERMAS RECIENTES (Top 5)
    // ----------------------------------------------------------------
    @GetMapping("/mermas-recientes")
    public ResponseEntity<List<Map<String, Object>>> getMermasRecientes() {
        List<Merma> mermas = mermaRepository.findTop5ByEstadoTrueOrderByFechahoraDesc();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Merma m : mermas) {
            Map<String, Object> row = new HashMap<>();
            row.put("fecha", m.getFechahora() != null
                    ? m.getFechahora().toLocalDate().toString() : "-");
            row.put("producto", m.getProducto() != null ? m.getProducto().getNombre() : "-");
            row.put("imagenUrl", m.getProducto() != null ? m.getProducto().getImagenUrl() : null);
            row.put("cantidad", m.getCantidad());
            row.put("motivo", m.getMotivoMerma() != null ? m.getMotivoMerma().getDescripcion() : "-");
            result.add(row);
        }
        return ResponseEntity.ok(result);
    }
}
