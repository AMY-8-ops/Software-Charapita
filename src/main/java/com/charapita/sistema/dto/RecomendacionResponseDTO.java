package com.charapita.sistema.dto;

import java.util.List;

public class RecomendacionResponseDTO {
    private List<Integer> recomendaciones;

    public RecomendacionResponseDTO() {}

    public RecomendacionResponseDTO(List<Integer> recomendaciones) {
        this.recomendaciones = recomendaciones;
    }

    public List<Integer> getRecomendaciones() {
        return recomendaciones;
    }

    public void setRecomendaciones(List<Integer> recomendaciones) {
        this.recomendaciones = recomendaciones;
    }
}
