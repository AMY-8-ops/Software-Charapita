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
@Table(name = "permiso")
public class Permiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idpermiso;
    private String descripcion;
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "idaccion")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Accion accion;

    @ManyToOne
    @JoinColumn(name = "idrecurso")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Recurso recurso;

    public Integer getIdpermiso() {
        return idpermiso;
    }

    public void setIdpermiso(Integer idpermiso) {
        this.idpermiso = idpermiso;
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

    public Accion getAccion() {
        return accion;
    }

    public void setAccion(Accion accion) {
        this.accion = accion;
    }

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    @Override
    public String toString() {
        return "Permiso [idpermiso=" + idpermiso + ", descripcion=" + descripcion + ", estado=" + estado + ", accion="
                + accion + ", recurso=" + recurso + "]";
    }
}
