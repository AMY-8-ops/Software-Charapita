package com.charapita.sistema.service.jpa;

import com.charapita.sistema.dto.CajaDashboardDTO;
import com.charapita.sistema.entity.Caja;
import com.charapita.sistema.entity.DetalleVenta;
import com.charapita.sistema.entity.MovimientoCaja;
import com.charapita.sistema.entity.Usuario;
import com.charapita.sistema.entity.Venta;
import com.charapita.sistema.repository.CajaRepository;
import com.charapita.sistema.repository.DetalleVentaRepository;
import com.charapita.sistema.repository.MovimientoCajaRepository;
import com.charapita.sistema.repository.UsuarioRepository;
import com.charapita.sistema.repository.VentaRepository;
import com.charapita.sistema.service.ICajaViewService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CajaViewServiceImpl implements ICajaViewService {

    private final MovimientoCajaRepository movimientoCajaRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CajaRepository cajaRepository;

    public CajaViewServiceImpl(MovimientoCajaRepository movimientoCajaRepository,
                               VentaRepository ventaRepository,
                               DetalleVentaRepository detalleVentaRepository,
                               UsuarioRepository usuarioRepository,
                               CajaRepository cajaRepository) {
        this.movimientoCajaRepository = movimientoCajaRepository;
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.usuarioRepository = usuarioRepository;
        this.cajaRepository = cajaRepository;
    }

    @Override
    public CajaDashboardDTO getCajaDashboardData() {
        CajaDashboardDTO dto = new CajaDashboardDTO();
        
        MovimientoCaja activeMovimiento = movimientoCajaRepository.findAll().stream()
                .filter(m -> m.getFhCierre() == null)
                .findFirst()
                .orElse(null);

        boolean isAbierta = activeMovimiento != null;
        java.math.BigDecimal totalInicial = java.math.BigDecimal.ZERO;
        java.math.BigDecimal salesEfectivo = java.math.BigDecimal.ZERO;
        java.math.BigDecimal salesElectronicas = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalVentas = java.math.BigDecimal.ZERO;
        java.math.BigDecimal montoEsperado = java.math.BigDecimal.ZERO;
        String nombreCajero = "Ninguno";
        String fechaApertura = "---";
        Integer activeId = null;

        if (isAbierta) {
            activeId = activeMovimiento.getIdmovimiento();
            totalInicial = activeMovimiento.getMontoinicial() != null ? activeMovimiento.getMontoinicial()
                    : java.math.BigDecimal.ZERO;
            if (activeMovimiento.getUsuario() != null) {
                nombreCajero = activeMovimiento.getUsuario().getNombre() + " " +
                        (activeMovimiento.getUsuario().getApellido() != null
                                ? activeMovimiento.getUsuario().getApellido()
                                : "");
            }
            if (activeMovimiento.getFhApertura() != null) {
                fechaApertura = activeMovimiento.getFhApertura()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            }

            final java.time.LocalDateTime openingTime = activeMovimiento.getFhApertura();
            List<Venta> sales = ventaRepository.findAll().stream()
                    .filter(v -> Boolean.TRUE.equals(v.getEstado()))
                    .filter(v -> v.getFecha() != null && !v.getFecha().isBefore(openingTime))
                    .toList();

            for (Venta v : sales) {
                List<DetalleVenta> details = detalleVentaRepository.findByIdIdventa(v.getIdventa());
                java.math.BigDecimal totalSale = details.stream()
                        .map(d -> d.getImporte() != null ? d.getImporte()
                                : java.math.BigDecimal.ZERO)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                if (v.getMetodoPago() != null
                        && v.getMetodoPago().getNombre().toLowerCase().contains("efectivo")) {
                    salesEfectivo = salesEfectivo.add(totalSale);
                } else {
                    salesElectronicas = salesElectronicas.add(totalSale);
                }
            }
            totalVentas = salesEfectivo.add(salesElectronicas);
            montoEsperado = totalInicial.add(salesEfectivo);
        }

        List<MovimientoCaja> allMovements = movimientoCajaRepository.findAll().stream()
                .sorted((m1, m2) -> m2.getFhApertura().compareTo(m1.getFhApertura()))
                .toList();

        List<java.util.Map<String, Object>> hist = new java.util.ArrayList<>();
        for (MovimientoCaja mc : allMovements) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("idmovimiento", mc.getIdmovimiento());
            map.put("fechaApertura",
                    mc.getFhApertura() != null
                            ? mc.getFhApertura().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            : "---");
            map.put("fechaCierre",
                    mc.getFhCierre() != null
                            ? mc.getFhCierre().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            : "-");
            map.put("cajero", mc.getUsuario() != null ? (mc.getUsuario().getNombre() + " "
                    + (mc.getUsuario().getApellido() != null ? mc.getUsuario().getApellido() : ""))
                    : "Sistema");
            map.put("montoInicial", mc.getMontoinicial() != null ? mc.getMontoinicial()
                    : java.math.BigDecimal.ZERO);
            map.put("montoFinal",
                    mc.getMontofinal() != null ? mc.getMontofinal() : java.math.BigDecimal.ZERO);
            map.put("diferencia",
                    mc.getDiferencia() != null ? mc.getDiferencia() : java.math.BigDecimal.ZERO);
            map.put("estado", mc.getFhCierre() != null ? "CERRADA" : "ABIERTA");

            java.time.LocalDateTime start = mc.getFhApertura();
            java.time.LocalDateTime end = mc.getFhCierre() != null ? mc.getFhCierre()
                    : java.time.LocalDateTime.now();

            List<Venta> salesInShift = ventaRepository.findAll().stream()
                    .filter(v -> Boolean.TRUE.equals(v.getEstado()))
                    .filter(v -> v.getFecha() != null && !v.getFecha().isBefore(start)
                            && !v.getFecha().isAfter(end))
                    .toList();

            java.math.BigDecimal vef = java.math.BigDecimal.ZERO;
            java.math.BigDecimal vel = java.math.BigDecimal.ZERO;
            for (Venta v : salesInShift) {
                List<DetalleVenta> details = detalleVentaRepository.findByIdIdventa(v.getIdventa());
                java.math.BigDecimal totalSale = details.stream()
                        .map(d -> d.getImporte() != null ? d.getImporte()
                                : java.math.BigDecimal.ZERO)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                if (v.getMetodoPago() != null
                        && v.getMetodoPago().getNombre().toLowerCase().contains("efectivo")) {
                    vef = vef.add(totalSale);
                } else {
                    vel = vel.add(totalSale);
                }
            }
            map.put("ventasEfectivo", vef);
            map.put("ventasElectronicas", vel);

            hist.add(map);
        }

        List<Usuario> listUsuarios = usuarioRepository.findAll().stream()
                .filter(u -> !Boolean.TRUE.equals(u.getEliminado()))
                .filter(u -> u.getEstado() != null && u.getEstado() == 1)
                .toList();

        List<Caja> listCajas = cajaRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .toList();

        if (listCajas.isEmpty()) {
            Caja defaultCaja = new Caja();
            defaultCaja.setNombre("Caja Principal");
            defaultCaja.setEstado(true);
            defaultCaja = cajaRepository.save(defaultCaja);
            listCajas = java.util.List.of(defaultCaja);
        }

        dto.setAbierta(isAbierta);
        dto.setActiveId(activeId);
        dto.setNombreCajero(nombreCajero);
        dto.setFechaApertura(fechaApertura);
        dto.setMontoInicial(totalInicial);
        dto.setVentasEfectivo(salesEfectivo);
        dto.setVentasElectronicas(salesElectronicas);
        dto.setTotalVentas(totalVentas);
        dto.setMontoEsperado(montoEsperado);
        dto.setHistorial(hist);
        dto.setUsuarios(listUsuarios);
        dto.setCajas(listCajas);

        return dto;
    }
}
