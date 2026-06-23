package com.charapita.sistema.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "metodopago")
public class MetodoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idmetodo;
    private String nombre;
    private Boolean estado;
    public Integer getIdmetodo() {
        return idmetodo;
    }
    public void setIdmetodo(Integer idmetodo) {
        this.idmetodo = idmetodo;
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
        return "MetodoPago [idmetodo=" + idmetodo + ", nombre=" + nombre + ", estado=" + estado + "]";
    }
}