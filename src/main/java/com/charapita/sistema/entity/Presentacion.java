package com.charapita.sistema.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "presentacion")
public class Presentacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idpresentacion;
    private String descripcion;
    private Boolean estado;
    public Integer getIdpresentacion() {
        return idpresentacion;
    }
    public void setIdpresentacion(Integer idpresentacion) {
        this.idpresentacion = idpresentacion;
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
    @Override
    public String toString() {
        return "Presentacion [idpresentacion=" + idpresentacion + ", descripcion=" + descripcion + ", estado=" + estado
                + "]";
    }
}