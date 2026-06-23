package com.charapita.sistema.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "recurso")
public class Recurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idrecurso;
    private String nombre;
    private Boolean estado;
    public Integer getIdrecurso() {
        return idrecurso;
    }
    public void setIdrecurso(Integer idrecurso) {
        this.idrecurso = idrecurso;
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
        return "Recurso [idrecurso=" + idrecurso + ", nombre=" + nombre + ", estado=" + estado + "]";
    }
}