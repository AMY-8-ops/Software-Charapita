package com.charapita.sistema.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalleingreso")
public class DetalleIngreso {
    @EmbeddedId
    private DetalleIngresoId id;

    @Column(name = "nro_lote")
    private String nroLote;
    private String cantidad;
    
    @Column(name = "fecha_vencimiento")
    private String fechaVencimiento;

    @ManyToOne
    @MapsId("idingreso")
    @JoinColumn(name = "idingreso")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private IngresoProduccion ingresoProduccion;

    @ManyToOne
    @MapsId("idproducto")
    @JoinColumn(name = "idproducto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Producto producto;

    public DetalleIngresoId getId() {
        return id;
    }

    public void setId(DetalleIngresoId id) {
        this.id = id;
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

    public IngresoProduccion getIngresoProduccion() {
        return ingresoProduccion;
    }

    public void setIngresoProduccion(IngresoProduccion ingresoProduccion) {
        this.ingresoProduccion = ingresoProduccion;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return "DetalleIngreso [id=" + id + ", nroLote=" + nroLote + ", cantidad=" + cantidad + ", fechaVencimiento="
                + fechaVencimiento + ", ingresoProduccion=" + ingresoProduccion + ", producto=" + producto + "]";
    }
}