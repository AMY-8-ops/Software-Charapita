package com.charapita.sistema.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idproducto;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "idcategoria")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "idpresentacion")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Presentacion presentacion;

    @jakarta.persistence.Transient
    private Integer stockMinimo;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Presentacion getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(Presentacion presentacion) {
        this.presentacion = presentacion;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getCodigo() {
        if (categoria == null || categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            return String.format("PROD%03d", idproducto);
        }
        String catName = categoria.getNombre().trim();
        String prefix = catName.length() >= 3 ? catName.substring(0, 3) : catName;
        return String.format("%s%03d", prefix.toUpperCase(), idproducto);
    }

    @Override
    public String toString() {
        return "Producto [idproducto=" + idproducto + ", nombre=" + nombre + ", precio=" + precio + ", descripcion="
                + descripcion + ", estado=" + estado + ", categoria=" + categoria + ", presentacion=" + presentacion
                + "]";
    }
}