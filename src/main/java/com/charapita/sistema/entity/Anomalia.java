package com.charapita.sistema.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "anomalia")
public class Anomalia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idanomalia;

    @Column(name = "fecha_deteccion")
    private LocalDateTime fechaDeteccion;

    @Column(name = "monto_evaluado")
    private BigDecimal montoEvaluado;

    private Double score;

    private Boolean leido;

    @ManyToOne
    @JoinColumn(name = "idmovimiento")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private MovimientoCaja movimientoCaja;

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

    public MovimientoCaja getMovimientoCaja() {
        return movimientoCaja;
    }

    public void setMovimientoCaja(MovimientoCaja movimientoCaja) {
        this.movimientoCaja = movimientoCaja;
    }

    @Override
    public String toString() {
        return "Anomalia [idanomalia=" + idanomalia + ", fechaDeteccion=" + fechaDeteccion + ", montoEvaluado="
                + montoEvaluado + ", score=" + score + ", leido=" + leido + ", movimientoCaja=" + movimientoCaja + "]";
    }
}
