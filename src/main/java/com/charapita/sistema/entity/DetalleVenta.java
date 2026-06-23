package com.charapita.sistema.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalleventa")
public class DetalleVenta {
    @EmbeddedId
    private DetalleVentaId id;

    private Integer cantidad;
    
    @Column(name = "precio_u")
    private BigDecimal precioU;
    private BigDecimal importe;

    @ManyToOne
    @MapsId("idproducto")
    @JoinColumn(name = "idproducto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Producto producto;

    @ManyToOne
    @MapsId("idventa")
    @JoinColumn(name = "idventa")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Venta venta;

    public DetalleVentaId getId() {
        return id;
    }

    public void setId(DetalleVentaId id) {
        this.id = id;
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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    @Override
    public String toString() {
        return "DetalleVenta [id=" + id + ", cantidad=" + cantidad + ", precioU=" + precioU + ", importe=" + importe
                + ", producto=" + producto + ", venta=" + venta + "]";
    }
}