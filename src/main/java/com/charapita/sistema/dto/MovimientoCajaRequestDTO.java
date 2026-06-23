package com.charapita.sistema.dto;

import java.math.BigDecimal;

public class MovimientoCajaRequestDTO {
    private Integer idcaja;
    private Integer idusuario;
    private BigDecimal montoinicial;
    public Integer getIdcaja() {
        return idcaja;
    }
    public void setIdcaja(Integer idcaja) {
        this.idcaja = idcaja;
    }
    public Integer getIdusuario() {
        return idusuario;
    }
    public void setIdusuario(Integer idusuario) {
        this.idusuario = idusuario;
    }
    public BigDecimal getMontoinicial() {
        return montoinicial;
    }
    public void setMontoinicial(BigDecimal montoinicial) {
        this.montoinicial = montoinicial;
    }
}