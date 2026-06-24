package com.charapita.sistema.dto;

import java.math.BigDecimal;

public class ReporteMermaDTO {
    private String motivo;
    private BigDecimal cantidad;
    private BigDecimal valor;

    public ReporteMermaDTO() {}

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
