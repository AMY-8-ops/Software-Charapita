package com.charapita.sistema.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.charapita.sistema.entity.Caja;
import com.charapita.sistema.entity.Usuario;

public class CajaDashboardDTO {
    private boolean isAbierta;
    private Integer activeId;
    private String nombreCajero;
    private String fechaApertura;
    private BigDecimal montoInicial;
    private BigDecimal ventasEfectivo;
    private BigDecimal ventasElectronicas;
    private BigDecimal totalVentas;
    private BigDecimal montoEsperado;
    private List<Map<String, Object>> historial;
    private List<Usuario> usuarios;
    private List<Caja> cajas;

    public CajaDashboardDTO() {}

    public boolean isAbierta() { return isAbierta; }
    public void setAbierta(boolean isAbierta) { this.isAbierta = isAbierta; }

    public Integer getActiveId() { return activeId; }
    public void setActiveId(Integer activeId) { this.activeId = activeId; }

    public String getNombreCajero() { return nombreCajero; }
    public void setNombreCajero(String nombreCajero) { this.nombreCajero = nombreCajero; }

    public String getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(String fechaApertura) { this.fechaApertura = fechaApertura; }

    public BigDecimal getMontoInicial() { return montoInicial; }
    public void setMontoInicial(BigDecimal montoInicial) { this.montoInicial = montoInicial; }

    public BigDecimal getVentasEfectivo() { return ventasEfectivo; }
    public void setVentasEfectivo(BigDecimal ventasEfectivo) { this.ventasEfectivo = ventasEfectivo; }

    public BigDecimal getVentasElectronicas() { return ventasElectronicas; }
    public void setVentasElectronicas(BigDecimal ventasElectronicas) { this.ventasElectronicas = ventasElectronicas; }

    public BigDecimal getTotalVentas() { return totalVentas; }
    public void setTotalVentas(BigDecimal totalVentas) { this.totalVentas = totalVentas; }

    public BigDecimal getMontoEsperado() { return montoEsperado; }
    public void setMontoEsperado(BigDecimal montoEsperado) { this.montoEsperado = montoEsperado; }

    public List<Map<String, Object>> getHistorial() { return historial; }
    public void setHistorial(List<Map<String, Object>> historial) { this.historial = historial; }

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }

    public List<Caja> getCajas() { return cajas; }
    public void setCajas(List<Caja> cajas) { this.cajas = cajas; }
}
