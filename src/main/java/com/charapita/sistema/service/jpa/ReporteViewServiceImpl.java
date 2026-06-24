package com.charapita.sistema.service.jpa;

import com.charapita.sistema.dto.ReporteDashboardDTO;
import com.charapita.sistema.dto.ReporteInventarioDTO;
import com.charapita.sistema.dto.ReporteMermaDTO;
import com.charapita.sistema.dto.ReporteVentaDTO;
import com.charapita.sistema.entity.Caja;
import com.charapita.sistema.entity.Categoria;
import com.charapita.sistema.entity.DetalleVenta;
import com.charapita.sistema.entity.Inventario;
import com.charapita.sistema.entity.Merma;
import com.charapita.sistema.entity.Usuario;
import com.charapita.sistema.entity.Venta;
import com.charapita.sistema.repository.CajaRepository;
import com.charapita.sistema.repository.CategoriaRepository;
import com.charapita.sistema.repository.DetalleVentaRepository;
import com.charapita.sistema.repository.InventarioRepository;
import com.charapita.sistema.repository.MermaRepository;
import com.charapita.sistema.repository.UsuarioRepository;
import com.charapita.sistema.repository.VentaRepository;
import com.charapita.sistema.service.IReporteViewService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteViewServiceImpl implements IReporteViewService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final MermaRepository mermaRepository;
    private final InventarioRepository inventarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final CajaRepository cajaRepository;

    public ReporteViewServiceImpl(VentaRepository ventaRepository,
                                  DetalleVentaRepository detalleVentaRepository,
                                  MermaRepository mermaRepository,
                                  InventarioRepository inventarioRepository,
                                  UsuarioRepository usuarioRepository,
                                  CategoriaRepository categoriaRepository,
                                  CajaRepository cajaRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.mermaRepository = mermaRepository;
        this.inventarioRepository = inventarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.cajaRepository = cajaRepository;
    }

    @Override
    public ReporteDashboardDTO getReporteDashboardData() {
        ReporteDashboardDTO dto = new ReporteDashboardDTO();

        // 1. VENTAS DETALLADO
        List<Venta> sales = ventaRepository.findAll().stream()
                .sorted((v1, v2) -> {
                    if (v1.getFecha() == null && v2.getFecha() == null) return 0;
                    if (v1.getFecha() == null) return 1;
                    if (v2.getFecha() == null) return -1;
                    return v2.getFecha().compareTo(v1.getFecha());
                })
                .toList();

        List<ReporteVentaDTO> histVentas = new java.util.ArrayList<>();
        java.math.BigDecimal totalVentasMonto = java.math.BigDecimal.ZERO;
        for (Venta v : sales) {
            ReporteVentaDTO item = new ReporteVentaDTO();
            item.setIdventa(v.getIdventa());
            item.setFecha(v.getFecha() != null
                    ? v.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : "---");
            item.setRawFecha(v.getFecha() != null ? v.getFecha().toLocalDate().toString() : "");

            String tipoComprobante = "Boleta";
            if (v.getTipoComprobante() != null && v.getTipoComprobante().getNombre() != null) {
                tipoComprobante = v.getTipoComprobante().getNombre();
            }
            item.setTipoComprobante(tipoComprobante);

            String numComprobante = v.getNroPedido() != null ? v.getNroPedido() : "---";
            item.setNumComprobante(numComprobante);

            String clienteName = "Cliente General";
            String clienteDoc = "00000000";
            if (v.getCliente() != null) {
                if (v.getCliente().getNombre() != null && !v.getCliente().getNombre().trim().isEmpty()) {
                    clienteName = v.getCliente().getNombre();
                } else if (v.getCliente().getRazonsocial() != null && !v.getCliente().getRazonsocial().trim().isEmpty()) {
                    clienteName = v.getCliente().getRazonsocial();
                }

                if (v.getCliente().getNroDocumento() != null && !v.getCliente().getNroDocumento().trim().isEmpty()) {
                    clienteDoc = v.getCliente().getNroDocumento();
                }
            }
            item.setClienteNombre(clienteName);
            item.setClienteDoc(clienteDoc);

            String metodoPago = "Efectivo";
            if (v.getMetodoPago() != null && v.getMetodoPago().getNombre() != null) {
                metodoPago = v.getMetodoPago().getNombre();
            }
            item.setMetodoPago(metodoPago);

            String vendedorName = "Sistema";
            if (v.getUsuario() != null) {
                String nom = v.getUsuario().getNombre() != null ? v.getUsuario().getNombre().trim() : "";
                String ape = v.getUsuario().getApellido() != null ? v.getUsuario().getApellido().trim() : "";
                String full = (nom + " " + ape).trim();
                vendedorName = full.isEmpty() ? "Usuario " + v.getUsuario().getIdusuario() : full;
            }
            item.setVendedor(vendedorName);
            item.setIdusuario(v.getUsuario() != null ? v.getUsuario().getIdusuario() : 0);
            item.setEstado(Boolean.TRUE.equals(v.getEstado()) ? "Completada" : "Anulada");
            item.setCompletada(Boolean.TRUE.equals(v.getEstado()));

            List<DetalleVenta> details = detalleVentaRepository.findByIdIdventa(v.getIdventa());
            java.math.BigDecimal total = details.stream()
                    .map(d -> d.getImporte() != null ? d.getImporte() : java.math.BigDecimal.ZERO)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            java.math.BigDecimal subtotal = total.divide(java.math.BigDecimal.valueOf(1.18), 2, java.math.RoundingMode.HALF_UP);
            java.math.BigDecimal igv = total.subtract(subtotal);

            item.setSubtotal(subtotal);
            item.setIgv(igv);
            item.setTotal(total);

            String cats = details.stream()
                    .map(d -> d.getProducto() != null && d.getProducto().getCategoria() != null
                            ? d.getProducto().getCategoria().getNombre() : "")
                    .filter(cat -> !cat.isEmpty())
                    .distinct()
                    .collect(java.util.stream.Collectors.joining(","));
            item.setCategorias(cats);

            histVentas.add(item);

            if (Boolean.TRUE.equals(v.getEstado())) {
                totalVentasMonto = totalVentasMonto.add(total);
            }
        }

        // 2. MERMAS Y PÉRDIDAS
        List<Merma> mermas = mermaRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getEstado()))
                .toList();

        java.util.Map<String, java.math.BigDecimal> mermaQty = new java.util.HashMap<>();
        java.util.Map<String, java.math.BigDecimal> mermaVal = new java.util.HashMap<>();
        for (Merma m : mermas) {
            String reason = m.getMotivoMerma() != null ? m.getMotivoMerma().getDescripcion() : "Otros";
            java.math.BigDecimal price = m.getProducto() != null && m.getProducto().getPrecio() != null
                    ? m.getProducto().getPrecio() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal qty = java.math.BigDecimal.valueOf(m.getCantidad() != null ? m.getCantidad() : 0);
            java.math.BigDecimal val = price.multiply(qty);

            mermaQty.put(reason, mermaQty.getOrDefault(reason, java.math.BigDecimal.ZERO).add(qty));
            mermaVal.put(reason, mermaVal.getOrDefault(reason, java.math.BigDecimal.ZERO).add(val));
        }

        List<ReporteMermaDTO> listMermas = new java.util.ArrayList<>();
        java.math.BigDecimal totalMermaQty = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalMermaVal = java.math.BigDecimal.ZERO;
        for (String reason : mermaQty.keySet()) {
            ReporteMermaDTO item = new ReporteMermaDTO();
            item.setMotivo(reason);
            item.setCantidad(mermaQty.get(reason));
            item.setValor(mermaVal.get(reason));
            listMermas.add(item);

            totalMermaQty = totalMermaQty.add(mermaQty.get(reason));
            totalMermaVal = totalMermaVal.add(mermaVal.get(reason));
        }

        double mermaPct = 0.0;
        if (totalVentasMonto.compareTo(java.math.BigDecimal.ZERO) > 0) {
            mermaPct = (totalMermaVal.doubleValue() / totalVentasMonto.doubleValue()) * 100.0;
            mermaPct = Math.round(mermaPct * 100.0) / 100.0;
        }

        // 3. INVENTARIO Y VALORIZACIÓN
        List<Inventario> inventarios = inventarioRepository.findAll().stream()
                .filter(i -> Boolean.TRUE.equals(i.getEstado()) && i.getProducto() != null
                        && Boolean.TRUE.equals(i.getProducto().getEstado()))
                .toList();

        java.util.Map<String, Integer> catProdCount = new java.util.HashMap<>();
        java.util.Map<String, java.math.BigDecimal> catStock = new java.util.HashMap<>();
        java.util.Map<String, java.math.BigDecimal> catVal = new java.util.HashMap<>();
        for (Inventario i : inventarios) {
            String catName = "General";
            if (i.getProducto().getCategoria() != null && i.getProducto().getCategoria().getNombre() != null
                    && !i.getProducto().getCategoria().getNombre().trim().isEmpty()) {
                catName = i.getProducto().getCategoria().getNombre();
            }
            java.math.BigDecimal price = i.getProducto().getPrecio() != null ? i.getProducto().getPrecio()
                    : java.math.BigDecimal.ZERO;
            java.math.BigDecimal stock = java.math.BigDecimal.valueOf(i.getStockactual() != null ? i.getStockactual() : 0);
            java.math.BigDecimal val = price.multiply(stock);

            catProdCount.put(catName, catProdCount.getOrDefault(catName, 0) + 1);
            catStock.put(catName, catStock.getOrDefault(catName, java.math.BigDecimal.ZERO).add(stock));
            catVal.put(catName, catVal.getOrDefault(catName, java.math.BigDecimal.ZERO).add(val));
        }

        List<ReporteInventarioDTO> listInventario = new java.util.ArrayList<>();
        int totalInvProducts = 0;
        java.math.BigDecimal totalInvStock = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalInvVal = java.math.BigDecimal.ZERO;
        for (String cat : catProdCount.keySet()) {
            ReporteInventarioDTO item = new ReporteInventarioDTO();
            item.setCategoria(cat);
            item.setProductos(catProdCount.get(cat));
            item.setStock(catStock.get(cat));
            item.setValor(catVal.get(cat));
            listInventario.add(item);

            totalInvProducts += catProdCount.get(cat);
            totalInvStock = totalInvStock.add(catStock.get(cat));
            totalInvVal = totalInvVal.add(catVal.get(cat));
        }

        // 4. DESPLEGABLES DE FILTROS
        List<Usuario> listUsuarios = usuarioRepository.findAll().stream()
                .filter(u -> !Boolean.TRUE.equals(u.getEliminado()))
                .filter(u -> u.getEstado() != null && u.getEstado() == 1)
                .toList();

        List<Categoria> listCategorias = categoriaRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .toList();

        List<Caja> listCajas = cajaRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .toList();

        String fechaInicio = java.time.LocalDate.now().minusMonths(1).toString();
        String fechaFin = java.time.LocalDate.now().toString();

        dto.setVentas(histVentas);
        dto.setTotalVentas(histVentas.size());
        dto.setTotalVentasMonto(totalVentasMonto);

        dto.setMermas(listMermas);
        dto.setTotalMermaQty(totalMermaQty);
        dto.setTotalMermaVal(totalMermaVal);
        dto.setMermaPorcentaje(mermaPct);

        dto.setInventarios(listInventario);
        dto.setTotalInvProducts(totalInvProducts);
        dto.setTotalInvStock(totalInvStock);
        dto.setTotalInvVal(totalInvVal);

        dto.setUsuarios(listUsuarios);
        dto.setCategorias(listCategorias);
        dto.setCajas(listCajas);

        dto.setFechaInicio(fechaInicio);
        dto.setFechaFin(fechaFin);

        return dto;
    }
}
