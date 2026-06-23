package com.charapita.sistema.dto;

public class MermaRequestDTO {
    private Integer cantidad;
    private Integer idmotivo;
    private Integer idproducto;
    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    public Integer getIdmotivo() {
        return idmotivo;
    }
    public void setIdmotivo(Integer idmotivo) {
        this.idmotivo = idmotivo;
    }
    public Integer getIdproducto() {
        return idproducto;
    }
    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
    }
}