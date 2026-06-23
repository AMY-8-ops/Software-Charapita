package com.charapita.sistema.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accion")
public class Accion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idaccion;
    private String nombre;
    private String descripcion;
    private Boolean estado;
    public Integer getIdaccion() {
        return idaccion;
    }
    public void setIdaccion(Integer idaccion) {
        this.idaccion = idaccion;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
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
        return "Accion [idaccion=" + idaccion + ", nombre=" + nombre + ", descripcion=" + descripcion + ", estado="
                + estado + "]";
    }
}