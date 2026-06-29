package com.charapita.sistema.dto;

import java.time.LocalDateTime;
import java.util.List;

public class VentaResponseDTO {
    private Integer idventa;
    private Integer clienteId;
    private String clienteTelefono;
    private String nroPedido;
    private String direccion;
    private String nroOperacion;

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteTelefono() {
        return clienteTelefono;
    }

    public void setClienteTelefono(String clienteTelefono) {
        this.clienteTelefono = clienteTelefono;
    }

    private LocalDateTime fecha;
    private Boolean estado;
    private String clienteNombre;
    private String usuarioNombre;
    private String tipoComprobanteDescripcion;
    private String metodoPagoDescripcion;
    private Boolean applyIgv;
    private List<DetalleVentaResponseDTO> detalles;

    public Boolean getApplyIgv() {
        return applyIgv;
    }

    public void setApplyIgv(Boolean applyIgv) {
        this.applyIgv = applyIgv;
    }

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

    public String getNroOperacion() {
        return nroOperacion;
    }

    public void setNroOperacion(String nroOperacion) {
        this.nroOperacion = nroOperacion;
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

    public java.math.BigDecimal getTotal() {
        if (detalles == null)
            return java.math.BigDecimal.ZERO;
        return detalles.stream()
                .map(d -> d.getImporte() != null ? d.getImporte() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public java.math.BigDecimal getSubtotal() {
        java.math.BigDecimal total = getTotal();
        if (Boolean.TRUE.equals(applyIgv)) {
            return total.divide(new java.math.BigDecimal("1.18"), 2, java.math.RoundingMode.HALF_UP);
        }
        return total;
    }

    public java.math.BigDecimal getIgv() {
        java.math.BigDecimal total = getTotal();
        if (Boolean.TRUE.equals(applyIgv)) {
            return total.subtract(getSubtotal());
        }
        return java.math.BigDecimal.ZERO;
    }
}
