package com.charapita.sistema.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class DetalleIngresoId implements Serializable {
    private Integer idingreso;
    private Integer idproducto;

    public DetalleIngresoId(){
    }
    public DetalleIngresoId(Integer idingreso, Integer idproducto){
        this.idingreso= idingreso;
        this.idproducto = idproducto;
    }

    public Integer getIdingreso() {
        return idingreso;
    }
    public void setIdingreso(Integer idingreso) {
        this.idingreso = idingreso;
    }
    public Integer getIdproducto() {
        return idproducto;
    }
    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.idingreso);
        hash = 17 * hash + Objects.hashCode(this.idproducto);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DetalleIngresoId other = (DetalleIngresoId) obj;
        if (!Objects.equals(this.idingreso, other.idingreso)) {
            return false;
        }
        return Objects.equals(this.idproducto, other.idproducto);
    }
    
    
}