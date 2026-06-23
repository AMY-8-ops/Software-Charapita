package com.charapita.sistema.dto;

public class DetalleIngresoRequestDTO {
    private Integer idproducto;
    private String nroLote;
    private String cantidad;
    private String fechaVencimiento;
    public Integer getIdproducto() {
        return idproducto;
    }
    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
    }
    public String getNroLote() {
        return nroLote;
    }
    public void setNroLote(String nroLote) {
        this.nroLote = nroLote;
    }
    public String getCantidad() {
        return cantidad;
    }
    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }
    public String getFechaVencimiento() {
        return fechaVencimiento;
    }
    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}