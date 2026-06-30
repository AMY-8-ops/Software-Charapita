package com.charapita.sistema.dto;

public class VentaDiariaDTO {
    private String fecha;
    private Double ingresos;

    public VentaDiariaDTO() {
    }

    public VentaDiariaDTO(String fecha, Double ingresos) {
        this.fecha = fecha;
        this.ingresos = ingresos;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Double getIngresos() {
        return ingresos;
    }

    public void setIngresos(Double ingresos) {
        this.ingresos = ingresos;
    }
}
