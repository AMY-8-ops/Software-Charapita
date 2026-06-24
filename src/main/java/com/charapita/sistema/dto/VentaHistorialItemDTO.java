package com.charapita.sistema.dto;

import java.math.BigDecimal;

public class VentaHistorialItemDTO {
    private Integer idventa;
    private String fecha;
    private String hora;
    private String rawFecha;
    private String tipoComprobante;
    private String numComprobante;
    private String clienteNombre;
    private String clienteDoc;
    private String metodoPago;
    private String vendedor;
    private String estado;
    private boolean isCompletada;
    private BigDecimal total;

    // Campos extra para reporte
    private BigDecimal subtotal;
    private BigDecimal igv;
    private String categorias;
    private Integer idusuario;

    public VentaHistorialItemDTO() {}

    public Integer getIdventa() { return idventa; }
    public void setIdventa(Integer idventa) { this.idventa = idventa; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getRawFecha() { return rawFecha; }
    public void setRawFecha(String rawFecha) { this.rawFecha = rawFecha; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

    public String getNumComprobante() { return numComprobante; }
    public void setNumComprobante(String numComprobante) { this.numComprobante = numComprobante; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getClienteDoc() { return clienteDoc; }
    public void setClienteDoc(String clienteDoc) { this.clienteDoc = clienteDoc; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getVendedor() { return vendedor; }
    public void setVendedor(String vendedor) { this.vendedor = vendedor; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean isCompletada() { return isCompletada; }
    public void setCompletada(boolean completada) { isCompletada = completada; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }

    public String getCategorias() { return categorias; }
    public void setCategorias(String categorias) { this.categorias = categorias; }

    public Integer getIdusuario() { return idusuario; }
    public void setIdusuario(Integer idusuario) { this.idusuario = idusuario; }
}
