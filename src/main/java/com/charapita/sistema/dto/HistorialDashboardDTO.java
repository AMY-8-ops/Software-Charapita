package com.charapita.sistema.dto;

import java.util.List;

public class HistorialDashboardDTO {
    private List<VentaHistorialItemDTO> ventas;
    private long totalVentas;
    private String fechaInicio;
    private String fechaFin;

    public HistorialDashboardDTO() {}

    public List<VentaHistorialItemDTO> getVentas() { return ventas; }
    public void setVentas(List<VentaHistorialItemDTO> ventas) { this.ventas = ventas; }

    public long getTotalVentas() { return totalVentas; }
    public void setTotalVentas(long totalVentas) { this.totalVentas = totalVentas; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
}
