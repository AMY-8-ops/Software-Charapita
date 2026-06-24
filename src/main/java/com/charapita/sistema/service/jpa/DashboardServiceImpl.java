package com.charapita.sistema.service.jpa;

import com.charapita.sistema.dto.DashboardResponseDTO;
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
import com.charapita.sistema.service.IDashboardService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class DashboardServiceImpl implements IDashboardService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final InventarioRepository inventarioRepository;
    private final MermaRepository mermaRepository;

    public DashboardServiceImpl(VentaRepository ventaRepository,
                                DetalleVentaRepository detalleVentaRepository,
                                MovimientoCajaRepository movimientoCajaRepository,
                                InventarioRepository inventarioRepository,
                                MermaRepository mermaRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.movimientoCajaRepository = movimientoCajaRepository;
        this.inventarioRepository = inventarioRepository;
        this.mermaRepository = mermaRepository;
    }

    @Override
    public DashboardResponseDTO getDashboardData() {
        DashboardResponseDTO dto = new DashboardResponseDTO();

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate yesterday = today.minusDays(1);

        List<Venta> allSales = ventaRepository.findAll().stream()
                .filter(v -> Boolean.TRUE.equals(v.getEstado()))
                .toList();

        java.math.BigDecimal ventasDia = java.math.BigDecimal.ZERO;
        long pedidosDia = 0;
        java.math.BigDecimal ventasAyer = java.math.BigDecimal.ZERO;
        long pedidosAyer = 0;

        for (Venta v : allSales) {
            if (v.getFecha() != null) {
                java.time.LocalDate saleDate = v.getFecha().toLocalDate();
                List<DetalleVenta> details = detalleVentaRepository.findByIdIdventa(v.getIdventa());
                java.math.BigDecimal saleTotal = details.stream()
                        .map(d -> d.getImporte() != null ? d.getImporte() : java.math.BigDecimal.ZERO)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                if (saleDate.equals(today)) {
                    ventasDia = ventasDia.add(saleTotal);
                    pedidosDia++;
                } else if (saleDate.equals(yesterday)) {
                    ventasAyer = ventasAyer.add(saleTotal);
                    pedidosAyer++;
                }
            }
        }

        double pctVentasVsAyer = 0.0;
        if (ventasAyer.compareTo(java.math.BigDecimal.ZERO) > 0) {
            pctVentasVsAyer = ((ventasDia.subtract(ventasAyer)).doubleValue() / ventasAyer.doubleValue()) * 100.0;
        } else if (ventasDia.compareTo(java.math.BigDecimal.ZERO) > 0) {
            pctVentasVsAyer = 100.0;
        }
        pctVentasVsAyer = Math.round(pctVentasVsAyer * 10.0) / 10.0;

        double pctPedidosVsAyer = 0.0;
        if (pedidosAyer > 0) {
            pctPedidosVsAyer = ((double)(pedidosDia - pedidosAyer) / pedidosAyer) * 100.0;
        } else if (pedidosDia > 0) {
            pctPedidosVsAyer = 100.0;
        }
        pctPedidosVsAyer = Math.round(pctPedidosVsAyer * 10.0) / 10.0;

        MovimientoCaja activeMovimiento = movimientoCajaRepository.findAll().stream()
                .filter(m -> m.getFhCierre() == null)
                .findFirst()
                .orElse(null);

        java.math.BigDecimal saldoCaja = java.math.BigDecimal.ZERO;
        if (activeMovimiento != null) {
            java.math.BigDecimal totalInicial = activeMovimiento.getMontoinicial() != null ? activeMovimiento.getMontoinicial() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal salesEfectivo = java.math.BigDecimal.ZERO;
            java.time.LocalDateTime openingTime = activeMovimiento.getFhApertura();
            
            List<Venta> salesInShift = allSales.stream()
                    .filter(v -> v.getFecha() != null && !v.getFecha().isBefore(openingTime))
                    .toList();

            for (Venta v : salesInShift) {
                if (v.getMetodoPago() != null && v.getMetodoPago().getNombre().toLowerCase().contains("efectivo")) {
                    List<DetalleVenta> details = detalleVentaRepository.findByIdIdventa(v.getIdventa());
                    java.math.BigDecimal saleTotal = details.stream()
                            .map(d -> d.getImporte() != null ? d.getImporte() : java.math.BigDecimal.ZERO)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                    salesEfectivo = salesEfectivo.add(saleTotal);
                }
            }
            saldoCaja = totalInicial.add(salesEfectivo);
        } else {
            MovimientoCaja lastClosed = movimientoCajaRepository.findAll().stream()
                    .filter(m -> m.getFhCierre() != null)
                    .sorted((m1, m2) -> m2.getFhCierre().compareTo(m1.getFhCierre()))
                    .findFirst()
                    .orElse(null);
            if (lastClosed != null && lastClosed.getMontofinal() != null) {
                saldoCaja = lastClosed.getMontofinal();
            }
        }

        List<Inventario> inventarios = inventarioRepository.findAll().stream()
                .filter(i -> Boolean.TRUE.equals(i.getEstado()) && i.getProducto() != null && Boolean.TRUE.equals(i.getProducto().getEstado()))
                .toList();
        long alertasStock = inventarios.stream()
                .filter(i -> i.getStockactual() != null && i.getStockminimo() != null && i.getStockactual() <= i.getStockminimo())
                .count();

        java.util.List<String> semanaLabels = new java.util.ArrayList<>();
        java.util.List<String> semanaValores = new java.util.ArrayList<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate d = today.minusDays(i);
            semanaLabels.add(d.format(dayFormatter));

            java.math.BigDecimal dailyTotal = java.math.BigDecimal.ZERO;
            for (Venta v : allSales) {
                if (v.getFecha() != null && v.getFecha().toLocalDate().equals(d)) {
                    List<DetalleVenta> details = detalleVentaRepository.findByIdIdventa(v.getIdventa());
                    java.math.BigDecimal saleTotal = details.stream()
                            .map(det -> det.getImporte() != null ? det.getImporte() : java.math.BigDecimal.ZERO)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                    dailyTotal = dailyTotal.add(saleTotal);
                }
            }
            semanaValores.add(dailyTotal.toString());
        }

        java.util.Map<String, java.math.BigDecimal> catSales = new java.util.HashMap<>();
        java.math.BigDecimal totalVentasTodasCat = java.math.BigDecimal.ZERO;

        for (Venta v : allSales) {
            List<DetalleVenta> details = detalleVentaRepository.findByIdIdventa(v.getIdventa());
            for (DetalleVenta dv : details) {
                if (dv.getProducto() != null && dv.getProducto().getCategoria() != null) {
                    String catName = dv.getProducto().getCategoria().getNombre();
                    java.math.BigDecimal imp = dv.getImporte() != null ? dv.getImporte() : java.math.BigDecimal.ZERO;
                    catSales.put(catName, catSales.getOrDefault(catName, java.math.BigDecimal.ZERO).add(imp));
                    totalVentasTodasCat = totalVentasTodasCat.add(imp);
                }
            }
        }

        java.util.List<String> catLabels = new java.util.ArrayList<>();
        java.util.List<String> catValores = new java.util.ArrayList<>();
        for (String cat : catSales.keySet()) {
            catLabels.add(cat);
            double pct = 0.0;
            if (totalVentasTodasCat.compareTo(java.math.BigDecimal.ZERO) > 0) {
                pct = (catSales.get(cat).doubleValue() / totalVentasTodasCat.doubleValue()) * 100.0;
            }
            pct = Math.round(pct * 10.0) / 10.0;
            catValores.add(String.valueOf(pct));
        }

        List<Inventario> bajoStock = inventarios.stream()
                .sorted(Comparator.comparing(i -> i.getStockactual() != null ? i.getStockactual() : 999999))
                .limit(5)
                .toList();

        List<Merma> mermasRecientes = mermaRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getEstado()))
                .sorted(Comparator.comparing(Merma::getFechahora, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();

        dto.setVentasDia(ventasDia);
        dto.setPedidosDia(pedidosDia);
        dto.setPctVentasVsAyer(pctVentasVsAyer);
        dto.setPctPedidosVsAyer(pctPedidosVsAyer);
        dto.setSaldoCaja(saldoCaja);
        dto.setAlertasStock(alertasStock);
        dto.setSemanaLabels(String.join(",", semanaLabels));
        dto.setSemanaValores(String.join(",", semanaValores));
        dto.setCatLabels(String.join(",", catLabels));
        dto.setCatValores(String.join(",", catValores));
        dto.setTotalVentasMonto(totalVentasTodasCat);
        dto.setBajoStock(bajoStock);
        dto.setMermasRecientes(mermasRecientes);
        dto.setCajaAbierta(activeMovimiento != null);

        return dto;
    }
}
