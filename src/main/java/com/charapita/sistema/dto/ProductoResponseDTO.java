package com.charapita.sistema.dto;

import java.math.BigDecimal;

public class ProductoResponseDTO {
    private Integer idproducto;
    private String nombre;
    private BigDecimal precio;
    private String categoria;
    private String presentacion;
    public Integer getIdproducto() {
        return idproducto;
    }
    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public String getPresentacion() {
        return presentacion;
    }
    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }
}