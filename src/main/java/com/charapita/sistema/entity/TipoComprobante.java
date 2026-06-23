package com.charapita.sistema.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipocomprobante")
public class TipoComprobante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idtipo;
    private String nombre;
    private Boolean estado;
    public Integer getIdtipo() {
        return idtipo;
    }
    public void setIdtipo(Integer idtipo) {
        this.idtipo = idtipo;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public Boolean getEstado() {
        return estado;
    }
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    @Override
    public String toString() {
        return "TipoComprobante [idtipo=" + idtipo + ", nombre=" + nombre + ", estado=" + estado + "]";
    }
}