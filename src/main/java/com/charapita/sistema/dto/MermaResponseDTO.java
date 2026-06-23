package com.charapita.sistema.dto;

import java.time.LocalDateTime;

public class MermaResponseDTO {
    private Integer idmerma;
    private LocalDateTime fechahora;
    private Integer cantidad;
    
    // Relaciones aplanadas
    private String motivoDescripcion; 
    private String productoNombre;
    private Boolean estado;

    public Boolean getEstado() {
        return estado;
    }
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Integer getIdmerma() {
        return idmerma;
    }
    public void setIdmerma(Integer idmerma) {
        this.idmerma = idmerma;
    }
    public LocalDateTime getFechahora() {
        return fechahora;
    }
    public void setFechahora(LocalDateTime fechahora) {
        this.fechahora = fechahora;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    public String getMotivoDescripcion() {
        return motivoDescripcion;
    }
    public void setMotivoDescripcion(String motivoDescripcion) {
        this.motivoDescripcion = motivoDescripcion;
    }
    public String getProductoNombre() {
        return productoNombre;
    }
    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }
}