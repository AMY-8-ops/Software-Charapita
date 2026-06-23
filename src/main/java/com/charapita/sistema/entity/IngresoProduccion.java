package com.charapita.sistema.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingresoproduccion")
public class IngresoProduccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idingreso;
    
    @Column(name = "dni_responsable")
    private String dniResponsable;
    
    @Column(name = "nombre_responsable")
    private String nombreResponsable;
    private String detalle;
    
    @Column(name = "estado", columnDefinition = "boolean default true")
    private Boolean estado = true;

    public Integer getIdingreso() {
        return idingreso;
    }
    public void setIdingreso(Integer idingreso) {
        this.idingreso = idingreso;
    }
    public String getDniResponsable() {
        return dniResponsable;
    }
    public void setDniResponsable(String dniResponsable) {
        this.dniResponsable = dniResponsable;
    }
    public String getNombreResponsable() {
        return nombreResponsable;
    }
    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }
    public String getDetalle() {
        return detalle;
    }
    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
    public Boolean getEstado() {
        return estado;
    }
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    @Override
    public String toString() {
        return "IngresoProduccion [idingreso=" + idingreso + ", dniResponsable=" + dniResponsable
                + ", nombreResponsable=" + nombreResponsable + ", detalle=" + detalle + ", estado=" + estado + "]";
    }
}