package com.charapita.sistema.dto;

import java.math.BigDecimal;

public class ReporteInventarioDTO {
    private String categoria;
    private Integer productos;
    private BigDecimal stock;
    private BigDecimal valor;

    public ReporteInventarioDTO() {}

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Integer getProductos() { return productos; }
    public void setProductos(Integer productos) { this.productos = productos; }

    public BigDecimal getStock() { return stock; }
    public void setStock(BigDecimal stock) { this.stock = stock; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
