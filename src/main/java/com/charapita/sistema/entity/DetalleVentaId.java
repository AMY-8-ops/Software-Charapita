package com.charapita.sistema.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class DetalleVentaId implements Serializable {
    
    private Integer idproducto;
    private Integer idventa;
    
    public DetalleVentaId() {
    }
    
    public DetalleVentaId(Integer idproducto, Integer idventa) {
        this.idproducto = idproducto;
        this.idventa = idventa;
    }
    public Integer getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
    }

    public Integer getIdventa() {
        return idventa;
    }

    public void setIdventa(Integer idventa) {
        this.idventa = idventa;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.idproducto);
        hash = 59 * hash + Objects.hashCode(this.idventa);
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
        final DetalleVentaId other = (DetalleVentaId) obj;
        if (!Objects.equals(this.idproducto, other.idproducto)) {
            return false;
        }
        return Objects.equals(this.idventa, other.idventa);
    }
}