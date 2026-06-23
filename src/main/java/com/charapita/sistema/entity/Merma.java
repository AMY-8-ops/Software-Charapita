package com.charapita.sistema.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "merma")
public class Merma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idmerma;
    private LocalDateTime fechahora;
    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "idmotivo")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MotivoMerma motivoMerma;

    @ManyToOne
    @JoinColumn(name = "idproducto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Producto producto;
    private Boolean estado;

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Integer getIdmerma() {
        return idmerma;
    }

    public void setIdmerma(Integer idmerma) {
        this.idmerma = idmerma;
    }

    public LocalDateTime getFechahora() {
        return fechahora;
    }

    public void setFechahora(LocalDateTime fechahora) {
        this.fechahora = fechahora;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public MotivoMerma getMotivoMerma() {
        return motivoMerma;
    }

    public void setMotivoMerma(MotivoMerma motivoMerma) {
        this.motivoMerma = motivoMerma;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return "Merma [idmerma=" + idmerma + ", fechahora=" + fechahora + ", cantidad=" + cantidad + ", motivoMerma="
                + motivoMerma + ", producto=" + producto + ", estado=" + estado + "]";
    }
}