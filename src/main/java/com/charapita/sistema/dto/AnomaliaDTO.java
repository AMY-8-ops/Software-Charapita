package com.charapita.sistema.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AnomaliaDTO {
    
    private Integer idanomalia;
    private LocalDateTime fechaDeteccion;
    private BigDecimal montoEvaluado;
    private Double score;
    private Boolean leido;
    private String nombreCaja;
    private String nombreCajero;

    public AnomaliaDTO() {}

    public AnomaliaDTO(Integer idanomalia, LocalDateTime fechaDeteccion, BigDecimal montoEvaluado, Double score,
            Boolean leido, String nombreCaja, String nombreCajero) {
        this.idanomalia = idanomalia;
        this.fechaDeteccion = fechaDeteccion;
        this.montoEvaluado = montoEvaluado;
        this.score = score;
        this.leido = leido;
        this.nombreCaja = nombreCaja;
        this.nombreCajero = nombreCajero;
    }

    public Integer getIdanomalia() {
        return idanomalia;
    }

    public void setIdanomalia(Integer idanomalia) {
        this.idanomalia = idanomalia;
    }

    public LocalDateTime getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(LocalDateTime fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }

    public BigDecimal getMontoEvaluado() {
        return montoEvaluado;
    }

    public void setMontoEvaluado(BigDecimal montoEvaluado) {
        this.montoEvaluado = montoEvaluado;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Boolean getLeido() {
        return leido;
    }

    public void setLeido(Boolean leido) {
        this.leido = leido;
    }

    public String getNombreCaja() {
        return nombreCaja;
    }

    public void setNombreCaja(String nombreCaja) {
        this.nombreCaja = nombreCaja;
    }

    public String getNombreCajero() {
        return nombreCajero;
    }

    public void setNombreCajero(String nombreCajero) {
        this.nombreCajero = nombreCajero;
    }
}
