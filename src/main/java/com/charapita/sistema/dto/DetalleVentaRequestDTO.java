package com.charapita.sistema.dto;

import java.math.BigDecimal;

public class DetalleVentaRequestDTO {
    private Integer idproducto;
    private Integer cantidad;
    private BigDecimal precioU;
    private BigDecimal importe;
    public Integer getIdproducto() {
        return idproducto;
    }
    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    public BigDecimal getPrecioU() {
        return precioU;
    }
    public void setPrecioU(BigDecimal precioU) {
        this.precioU = precioU;
    }
    public BigDecimal getImporte() {
        return importe;
    }
    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }
}