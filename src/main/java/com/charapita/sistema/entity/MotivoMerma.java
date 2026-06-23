package com.charapita.sistema.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "motivomerma")
public class MotivoMerma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idmotivo;
    private String descripcion;
    private Boolean estado;
    public Boolean getEstado() {
        return estado;
    }
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    public Integer getIdmotivo() {
        return idmotivo;
    }
    public void setIdmotivo(Integer idmotivo) {
        this.idmotivo = idmotivo;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    @Override
    public String toString() {
        return "MotivoMerma [idmotivo=" + idmotivo + ", descripcion=" + descripcion + ", estado=" + estado + "]";
    }
}