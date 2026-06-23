package com.charapita.sistema.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipocliente")
public class TipoCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idtipocliente;
    private String nombre;
    private Boolean estado;
    public Boolean getEstado() {
        return estado;
    }
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    public Integer getIdtipocliente() {
        return idtipocliente;
    }
    public void setIdtipocliente(Integer idtipocliente) {
        this.idtipocliente = idtipocliente;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    @Override
    public String toString() {
        return "TipoCliente [idtipocliente=" + idtipocliente + ", nombre=" + nombre + ", estado=" + estado + "]";
    }
}