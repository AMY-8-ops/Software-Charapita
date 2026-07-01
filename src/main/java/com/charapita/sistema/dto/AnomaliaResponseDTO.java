package com.charapita.sistema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnomaliaResponseDTO {
    @JsonProperty("is_anomaly")
    private Boolean isAnomaly;
    
    private Double score;

    public AnomaliaResponseDTO() {}

    public AnomaliaResponseDTO(Boolean isAnomaly, Double score) {
        this.isAnomaly = isAnomaly;
        this.score = score;
    }

    public Boolean getIsAnomaly() {
        return isAnomaly;
    }

    public void setIsAnomaly(Boolean isAnomaly) {
        this.isAnomaly = isAnomaly;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
