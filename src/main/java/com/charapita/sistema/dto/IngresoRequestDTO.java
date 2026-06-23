package com.charapita.sistema.dto;

import java.util.List;

public class IngresoRequestDTO {
    private String dniResponsable;
    private String nombreResponsable;
    private String detalle;

    private List<DetalleIngresoRequestDTO> detalles;

    public String getDniResponsable() {
        return dniResponsable;
    }

    public void setDniResponsable(String dniResponsable) {
        this.dniResponsable = dniResponsable;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public List<DetalleIngresoRequestDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleIngresoRequestDTO> detalles) {
        this.detalles = detalles;
    }
}