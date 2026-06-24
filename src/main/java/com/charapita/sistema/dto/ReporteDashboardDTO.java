package com.charapita.sistema.dto;

import java.math.BigDecimal;
import java.util.List;
import com.charapita.sistema.entity.Caja;
import com.charapita.sistema.entity.Categoria;
import com.charapita.sistema.entity.Usuario;

public class ReporteDashboardDTO {
    private List<ReporteVentaDTO> ventas;
    private long totalVentas;
    private BigDecimal totalVentasMonto;

    private List<ReporteMermaDTO> mermas;
    private BigDecimal totalMermaQty;
    private BigDecimal totalMermaVal;
    private double mermaPorcentaje;

    private List<ReporteInventarioDTO> inventarios;
    private Integer totalInvProducts;
    private BigDecimal totalInvStock;
    private BigDecimal totalInvVal;

    private List<Usuario> usuarios;
    private List<Categoria> categorias;
    private List<Caja> cajas;

    private String fechaInicio;
    private String fechaFin;

    public ReporteDashboardDTO() {}

    public List<ReporteVentaDTO> getVentas() { return ventas; }
    public void setVentas(List<ReporteVentaDTO> ventas) { this.ventas = ventas; }

    public long getTotalVentas() { return totalVentas; }
    public void setTotalVentas(long totalVentas) { this.totalVentas = totalVentas; }

    public BigDecimal getTotalVentasMonto() { return totalVentasMonto; }
    public void setTotalVentasMonto(BigDecimal totalVentasMonto) { this.totalVentasMonto = totalVentasMonto; }

    public List<ReporteMermaDTO> getMermas() { return mermas; }
    public void setMermas(List<ReporteMermaDTO> mermas) { this.mermas = mermas; }

    public BigDecimal getTotalMermaQty() { return totalMermaQty; }
    public void setTotalMermaQty(BigDecimal totalMermaQty) { this.totalMermaQty = totalMermaQty; }

    public BigDecimal getTotalMermaVal() { return totalMermaVal; }
    public void setTotalMermaVal(BigDecimal totalMermaVal) { this.totalMermaVal = totalMermaVal; }

    public double getMermaPorcentaje() { return mermaPorcentaje; }
    public void setMermaPorcentaje(double mermaPorcentaje) { this.mermaPorcentaje = mermaPorcentaje; }

    public List<ReporteInventarioDTO> getInventarios() { return inventarios; }
    public void setInventarios(List<ReporteInventarioDTO> inventarios) { this.inventarios = inventarios; }

    public Integer getTotalInvProducts() { return totalInvProducts; }
    public void setTotalInvProducts(Integer totalInvProducts) { this.totalInvProducts = totalInvProducts; }

    public BigDecimal getTotalInvStock() { return totalInvStock; }
    public void setTotalInvStock(BigDecimal totalInvStock) { this.totalInvStock = totalInvStock; }

    public BigDecimal getTotalInvVal() { return totalInvVal; }
    public void setTotalInvVal(BigDecimal totalInvVal) { this.totalInvVal = totalInvVal; }

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }

    public List<Categoria> getCategorias() { return categorias; }
    public void setCategorias(List<Categoria> categorias) { this.categorias = categorias; }

    public List<Caja> getCajas() { return cajas; }
    public void setCajas(List<Caja> cajas) { this.cajas = cajas; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
}
