package com.charapita.sistema.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "caja")
public class Caja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idcaja;
    private String nombre;
    private Boolean estado;
    public Integer getIdcaja() {
        return idcaja;
    }
    public void setIdcaja(Integer idcaja) {
        this.idcaja = idcaja;
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
        return "Caja [idcaja=" + idcaja + ", nombre=" + nombre + ", estado=" + estado + "]";
    }
}