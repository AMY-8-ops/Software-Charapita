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
@Table(name = "movimientocaja")
public class MovimientoCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idmovimiento;
    
    @Column(name = "fh_apertura")
    private LocalDateTime fhApertura;
    private BigDecimal montoinicial;
    
    @Column(name = "fh_cierre")
    private LocalDateTime fhCierre;
    private BigDecimal montofinal;
    private BigDecimal diferencia;

    @ManyToOne
    @JoinColumn(name = "idcaja")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Caja caja;

    @ManyToOne
    @JoinColumn(name = "idusuario")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    public Integer getIdmovimiento() {
        return idmovimiento;
    }

    public void setIdmovimiento(Integer idmovimiento) {
        this.idmovimiento = idmovimiento;
    }

    public LocalDateTime getFhApertura() {
        return fhApertura;
    }

    public void setFhApertura(LocalDateTime fhApertura) {
        this.fhApertura = fhApertura;
    }

    public BigDecimal getMontoinicial() {
        return montoinicial;
    }

    public void setMontoinicial(BigDecimal montoinicial) {
        this.montoinicial = montoinicial;
    }

    public LocalDateTime getFhCierre() {
        return fhCierre;
    }

    public void setFhCierre(LocalDateTime fhCierre) {
        this.fhCierre = fhCierre;
    }

    public BigDecimal getMontofinal() {
        return montofinal;
    }

    public void setMontofinal(BigDecimal montofinal) {
        this.montofinal = montofinal;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(BigDecimal diferencia) {
        this.diferencia = diferencia;
    }

    public Caja getCaja() {
        return caja;
    }

    public void setCaja(Caja caja) {
        this.caja = caja;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "MovimientoCaja [idmovimiento=" + idmovimiento + ", fhApertura=" + fhApertura + ", montoinicial="
                + montoinicial + ", fhCierre=" + fhCierre + ", montofinal=" + montofinal + ", diferencia=" + diferencia
                + ", caja=" + caja + ", usuario=" + usuario + "]";
    }
}