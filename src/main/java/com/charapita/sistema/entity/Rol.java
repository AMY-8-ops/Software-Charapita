package com.charapita.sistema.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rol")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idrol;
    private String nombre;
    private Boolean estado;

    public Integer getIdrol() {
        return idrol;
    }

    public void setIdrol(Integer idrol) {
        this.idrol = idrol;
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

    public boolean hasNuevaVenta() {
        return true;
    }

    public boolean hasClientes() {
        return true;
    }

    public boolean hasProductos() {
        return true;
    }

    public boolean hasVentasHistorial() {
        return true;
    }

    public boolean hasReportes() {
        return true;
    }

    public boolean hasCaja() {
        return true;
    }

    public boolean hasConfiguracion() {
        return true;
    }

    @Override
    public String toString() {
        return "Rol [idrol=" + idrol + ", nombre=" + nombre + ", estado=" + estado + "]";
    }
}