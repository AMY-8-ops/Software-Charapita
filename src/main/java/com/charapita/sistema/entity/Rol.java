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

    private Boolean modNuevaVenta = false;
    private Boolean modClientes = false;
    private Boolean modProductos = false;
    private Boolean modVentasHistorial = false;
    private Boolean modReportes = false;
    private Boolean modCaja = false;
    private Boolean modConfiguracion = false;

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

    public Boolean getModNuevaVenta() {
        return modNuevaVenta;
    }

    public void setModNuevaVenta(Boolean modNuevaVenta) {
        this.modNuevaVenta = modNuevaVenta;
    }

    public Boolean getModClientes() {
        return modClientes;
    }

    public void setModClientes(Boolean modClientes) {
        this.modClientes = modClientes;
    }

    public Boolean getModProductos() {
        return modProductos;
    }

    public void setModProductos(Boolean modProductos) {
        this.modProductos = modProductos;
    }

    public Boolean getModVentasHistorial() {
        return modVentasHistorial;
    }

    public void setModVentasHistorial(Boolean modVentasHistorial) {
        this.modVentasHistorial = modVentasHistorial;
    }

    public Boolean getModReportes() {
        return modReportes;
    }

    public void setModReportes(Boolean modReportes) {
        this.modReportes = modReportes;
    }

    public Boolean getModCaja() {
        return modCaja;
    }

    public void setModCaja(Boolean modCaja) {
        this.modCaja = modCaja;
    }

    public Boolean getModConfiguracion() {
        return modConfiguracion;
    }

    public void setModConfiguracion(Boolean modConfiguracion) {
        this.modConfiguracion = modConfiguracion;
    }

    @Override
    public String toString() {
        return "Rol [idrol=" + idrol + ", nombre=" + nombre + ", estado=" + estado + "]";
    }
}