package com.charapita.sistema.dto;

import java.math.BigDecimal;
import java.util.List;
import com.charapita.sistema.entity.Inventario;
import com.charapita.sistema.entity.Merma;

public class DashboardResponseDTO {
    private BigDecimal ventasDia;
    private long pedidosDia;
    private double pctVentasVsAyer;
    private double pctPedidosVsAyer;
    private BigDecimal saldoCaja;
    private long alertasStock;

    private String semanaLabels;
    private String semanaValores;

    private String catLabels;
    private String catValores;
    private BigDecimal totalVentasMonto;

    private List<Inventario> bajoStock;
    private List<Merma> mermasRecientes;
    private boolean isCajaAbierta;

    public DashboardResponseDTO() {}

    public BigDecimal getVentasDia() { return ventasDia; }
    public void setVentasDia(BigDecimal ventasDia) { this.ventasDia = ventasDia; }

    public long getPedidosDia() { return pedidosDia; }
    public void setPedidosDia(long pedidosDia) { this.pedidosDia = pedidosDia; }

    public double getPctVentasVsAyer() { return pctVentasVsAyer; }
    public void setPctVentasVsAyer(double pctVentasVsAyer) { this.pctVentasVsAyer = pctVentasVsAyer; }

    public double getPctPedidosVsAyer() { return pctPedidosVsAyer; }
    public void setPctPedidosVsAyer(double pctPedidosVsAyer) { this.pctPedidosVsAyer = pctPedidosVsAyer; }

    public BigDecimal getSaldoCaja() { return saldoCaja; }
    public void setSaldoCaja(BigDecimal saldoCaja) { this.saldoCaja = saldoCaja; }

    public long getAlertasStock() { return alertasStock; }
    public void setAlertasStock(long alertasStock) { this.alertasStock = alertasStock; }

    public String getSemanaLabels() { return semanaLabels; }
    public void setSemanaLabels(String semanaLabels) { this.semanaLabels = semanaLabels; }

    public String getSemanaValores() { return semanaValores; }
    public void setSemanaValores(String semanaValores) { this.semanaValores = semanaValores; }

    public String getCatLabels() { return catLabels; }
    public void setCatLabels(String catLabels) { this.catLabels = catLabels; }

    public String getCatValores() { return catValores; }
    public void setCatValores(String catValores) { this.catValores = catValores; }

    public BigDecimal getTotalVentasMonto() { return totalVentasMonto; }
    public void setTotalVentasMonto(BigDecimal totalVentasMonto) { this.totalVentasMonto = totalVentasMonto; }

    public List<Inventario> getBajoStock() { return bajoStock; }
    public void setBajoStock(List<Inventario> bajoStock) { this.bajoStock = bajoStock; }

    public List<Merma> getMermasRecientes() { return mermasRecientes; }
    public void setMermasRecientes(List<Merma> mermasRecientes) { this.mermasRecientes = mermasRecientes; }

    public boolean isCajaAbierta() { return isCajaAbierta; }
    public void setCajaAbierta(boolean cajaAbierta) { isCajaAbierta = cajaAbierta; }
}
