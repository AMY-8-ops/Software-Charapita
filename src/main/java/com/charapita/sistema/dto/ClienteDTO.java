package com.charapita.sistema.dto;

public class ClienteDTO {
    private Integer idcliente;
    private String razonsocial;
    private String nombre;
    private String nroDocumento;
    private Boolean estado;
    
    private Integer idtipocliente; 
    
    private String tipoClienteNombre;

    public Integer getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(Integer idcliente) {
        this.idcliente = idcliente;
    }

    public String getRazonsocial() {
        return razonsocial;
    }

    public void setRazonsocial(String razonsocial) {
        this.razonsocial = razonsocial;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Integer getIdtipocliente() {
        return idtipocliente;
    }

    public void setIdtipocliente(Integer idtipocliente) {
        this.idtipocliente = idtipocliente;
    }

    public String getTipoClienteNombre() {
        return tipoClienteNombre;
    }

    public void setTipoClienteNombre(String tipoClienteNombre) {
        this.tipoClienteNombre = tipoClienteNombre;
    } 
}