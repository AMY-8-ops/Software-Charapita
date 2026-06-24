package com.charapita.sistema.service.jpa;

import com.charapita.sistema.dto.HistorialDashboardDTO;
import com.charapita.sistema.dto.VentaHistorialItemDTO;
import com.charapita.sistema.entity.DetalleVenta;
import com.charapita.sistema.entity.Venta;
import com.charapita.sistema.repository.DetalleVentaRepository;
import com.charapita.sistema.repository.VentaRepository;
import com.charapita.sistema.service.IHistorialViewService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class HistorialViewServiceImpl implements IHistorialViewService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public HistorialViewServiceImpl(VentaRepository ventaRepository,
                                    DetalleVentaRepository detalleVentaRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    @Override
    public HistorialDashboardDTO getHistorialDashboardData() {
        HistorialDashboardDTO dto = new HistorialDashboardDTO();

        List<Venta> sales = ventaRepository.findAll().stream()
                .sorted((v1, v2) -> {
                    if (v1.getFecha() == null && v2.getFecha() == null)
                        return 0;
                    if (v1.getFecha() == null)
                        return 1;
                    if (v2.getFecha() == null)
                        return -1;
                    return v2.getFecha().compareTo(v1.getFecha());
                })
                .toList();

        List<VentaHistorialItemDTO> histVentas = new java.util.ArrayList<>();
        for (Venta v : sales) {
            VentaHistorialItemDTO item = new VentaHistorialItemDTO();
            item.setIdventa(v.getIdventa());
            item.setFecha(v.getFecha() != null
                    ? v.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "---");
            item.setHora(v.getFecha() != null ? v.getFecha().format(DateTimeFormatter.ofPattern("HH:mm"))
                    : "---");
            item.setRawFecha(v.getFecha() != null ? v.getFecha().toLocalDate().toString() : "");
            item.setTipoComprobante(v.getTipoComprobante() != null ? v.getTipoComprobante().getNombre() : "Boleta");
            item.setNumComprobante(v.getNroPedido() != null ? v.getNroPedido() : "---");

            String clienteName = "Ocasional";
            String clienteDoc = "00000000";
            if (v.getCliente() != null) {
                clienteName = v.getCliente().getNombre() != null
                        && !v.getCliente().getNombre().isEmpty() ? v.getCliente().getNombre()
                        : v.getCliente().getRazonsocial();
                clienteDoc = v.getCliente().getNroDocumento();
            }
            item.setClienteNombre(clienteName);
            item.setClienteDoc(clienteDoc);
            item.setMetodoPago(v.getMetodoPago() != null ? v.getMetodoPago().getNombre() : "Efectivo");
            item.setVendedor(v.getUsuario() != null ? (v.getUsuario().getNombre() + " "
                    + (v.getUsuario().getApellido() != null ? v.getUsuario().getApellido() : ""))
                    : "Sistema");
            item.setEstado(Boolean.TRUE.equals(v.getEstado()) ? "Completada" : "Anulada");
            item.setCompletada(Boolean.TRUE.equals(v.getEstado()));

            List<DetalleVenta> details = detalleVentaRepository.findByIdIdventa(v.getIdventa());
            java.math.BigDecimal total = details.stream()
                    .map(d -> d.getImporte() != null ? d.getImporte() : java.math.BigDecimal.ZERO)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            item.setTotal(total);

            histVentas.add(item);
        }

        String fechaInicio = java.time.LocalDate.now().minusMonths(1).toString();
        String fechaFin = java.time.LocalDate.now().toString();

        dto.setVentas(histVentas);
        dto.setTotalVentas(histVentas.size());
        dto.setFechaInicio(fechaInicio);
        dto.setFechaFin(fechaFin);

        return dto;
    }
}
