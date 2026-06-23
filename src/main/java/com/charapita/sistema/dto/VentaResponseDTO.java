package com.charapita.sistema.dto;

import java.time.LocalDateTime;
import java.util.List;

public class VentaResponseDTO {
    private Integer idventa;
    private String nroPedido;
    private String direccion;
    private LocalDateTime fecha;
    private Boolean estado;
    private String clienteNombre;
    private String usuarioNombre;
    private String tipoComprobanteDescripcion;
    private String metodoPagoDescripcion;
    private List<DetalleVentaResponseDTO> detalles;

    public Integer getIdventa() {
        return idventa;
    }

    public void setIdventa(Integer idventa) {
        this.idventa = idventa;
    }

    public String getNroPedido() {
        return nroPedido;
    }

    public void setNroPedido(String nroPedido) {
        this.nroPedido = nroPedido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getTipoComprobanteDescripcion() {
        return tipoComprobanteDescripcion;
    }

    public void setTipoComprobanteDescripcion(String tipoComprobanteDescripcion) {
        this.tipoComprobanteDescripcion = tipoComprobanteDescripcion;
    }

    public String getMetodoPagoDescripcion() {
        return metodoPagoDescripcion;
    }

    public void setMetodoPagoDescripcion(String metodoPagoDescripcion) {
        this.metodoPagoDescripcion = metodoPagoDescripcion;
    }

    public List<DetalleVentaResponseDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaResponseDTO> detalles) {
        this.detalles = detalles;
    }
}
