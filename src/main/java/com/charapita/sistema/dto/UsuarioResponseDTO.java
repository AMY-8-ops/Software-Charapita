package com.charapita.sistema.dto;

public class UsuarioResponseDTO {
    private Integer idusuario;
    private String nombreCompleto;
    private String correo;
    private String rol;
    private Integer idrol;
    
    public Integer getIdusuario() {
        return idusuario;
    }
    public void setIdusuario(Integer idusuario) {
        this.idusuario = idusuario;
    }
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }
    public Integer getIdrol() {
        return idrol;
    }
    public void setIdrol(Integer idrol) {
        this.idrol = idrol;
    }
}