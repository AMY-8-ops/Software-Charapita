package com.charapita.sistema.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idusuario;
    private Integer dni;
    private String nombre;
    private String apellido;
    private String direccion;
    private String correo;
    private String contrasena;
    private Integer estado;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;
    private Boolean eliminado;

    @ManyToOne
    @JoinColumn(name = "idrol")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Rol rol;

    public Integer getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(Integer idusuario) {
        this.idusuario = idusuario;
    }

    public Integer getDni() {
        return dni;
    }

    public void setDni(Integer dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getIniciales() {
        String init = "";
        if (nombre != null && !nombre.trim().isEmpty()) {
            init += nombre.trim().substring(0, 1).toUpperCase();
        }
        if (apellido != null && !apellido.trim().isEmpty()) {
            init += apellido.trim().substring(0, 1).toUpperCase();
        }
        return init.isEmpty() ? "U" : init;
    }

    @Override
    public String toString() {
        return "Usuario [idusuario=" + idusuario + ", dni=" + dni + ", nombre=" + nombre + ", apellido=" + apellido
                + ", direccion=" + direccion + ", correo=" + correo + ", contrasena=" + contrasena + ", estado="
                + estado + ", ultimoAcceso=" + ultimoAcceso + ", eliminado=" + eliminado + ", rol=" + rol + "]";
    }
}