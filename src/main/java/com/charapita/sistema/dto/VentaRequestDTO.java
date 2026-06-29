package com.charapita.sistema.dto;

import java.util.List;

public class VentaRequestDTO {
    private Integer idcliente;
    private Integer idusuario;
    private Integer idtipocomprobante;
    private Integer idmetodopago;
    private String nroOperacion;
    private String nroPedido;

    private List<DetalleVentaRequestDTO> detalles;

    public String getNroPedido() {
        return nroPedido;
    }

    public void setNroPedido(String nroPedido) {
        this.nroPedido = nroPedido;
    }

    public String getNroOperacion() {
        return nroOperacion;
    }

    public void setNroOperacion(String nroOperacion) {
        this.nroOperacion = nroOperacion;
    }

    public Integer getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(Integer idcliente) {
        this.idcliente = idcliente;
    }

    public Integer getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(Integer idusuario) {
        this.idusuario = idusuario;
    }

    public Integer getIdtipocomprobante() {
        return idtipocomprobante;
    }

    public void setIdtipocomprobante(Integer idtipocomprobante) {
        this.idtipocomprobante = idtipocomprobante;
    }

    public Integer getIdmetodopago() {
        return idmetodopago;
    }

    public void setIdmetodopago(Integer idmetodopago) {
        this.idmetodopago = idmetodopago;
    }

    public List<DetalleVentaRequestDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaRequestDTO> detalles) {
        this.detalles = detalles;
    }
}