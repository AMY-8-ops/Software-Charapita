package com.charapita.sistema.dto;

public class PrediccionResponseDTO {
    private String fecha;
    private Double prediccion;

    public PrediccionResponseDTO() {
    }

    public PrediccionResponseDTO(String fecha, Double prediccion) {
        this.fecha = fecha;
        this.prediccion = prediccion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Double getPrediccion() {
        return prediccion;
    }

    public void setPrediccion(Double prediccion) {
        this.prediccion = prediccion;
    }
}
