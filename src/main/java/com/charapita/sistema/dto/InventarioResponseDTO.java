package com.charapita.sistema.dto;

public class InventarioResponseDTO {
    private Integer idinventario;
    private Integer idproducto;
    
    private String nombreProducto;
    private String categoria;
    
    private Integer stockactual;
    private Integer stockminimo;

    private Boolean alertaStockAbajo;

    public Integer getIdinventario() {
        return idinventario;
    }

    public void setIdinventario(Integer idinventario) {
        this.idinventario = idinventario;
    }

    public Integer getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getStockactual() {
        return stockactual;
    }

    public void setStockactual(Integer stockactual) {
        this.stockactual = stockactual;
    }

    public Integer getStockminimo() {
        return stockminimo;
    }

    public void setStockminimo(Integer stockminimo) {
        this.stockminimo = stockminimo;
    }

    public Boolean getAlertaStockAbajo() {
        return alertaStockAbajo;
    }

    public void setAlertaStockAbajo(Boolean alertaStockAbajo) {
        this.alertaStockAbajo = alertaStockAbajo;
    } 
}