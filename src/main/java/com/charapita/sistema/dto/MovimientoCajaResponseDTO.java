package com.charapita.sistema.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoCajaResponseDTO {
    private Integer idmovimiento;
    private LocalDateTime fhApertura;
    private LocalDateTime fhCierre;
    private BigDecimal montoinicial;
    private BigDecimal montofinal;
    private BigDecimal diferencia;
    
    private String nombreCaja;
    private String nombreCajero;
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
    public LocalDateTime getFhCierre() {
        return fhCierre;
    }
    public void setFhCierre(LocalDateTime fhCierre) {
        this.fhCierre = fhCierre;
    }
    public BigDecimal getMontoinicial() {
        return montoinicial;
    }
    public void setMontoinicial(BigDecimal montoinicial) {
        this.montoinicial = montoinicial;
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