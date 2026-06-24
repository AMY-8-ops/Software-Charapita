package com.charapita.sistema.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rol")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idrol;
    private String nombre;
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "idpermiso")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Permiso permiso;

    public Integer getIdrol() {
        return idrol;
    }

    public void setIdrol(Integer idrol) {
        this.idrol = idrol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Permiso getPermiso() {
        return permiso;
    }

    public void setPermiso(Permiso permiso) {
        this.permiso = permiso;
    }

    private boolean isPermisoActivo() {
        return permiso != null && Boolean.TRUE.equals(permiso.getEstado());
    }

    public boolean hasNuevaVenta() {
        if (!isPermisoActivo() || permiso.getRecurso() == null) return false;
        String r = permiso.getRecurso().getNombre();
        return "SISTEMA_TOTAL".equalsIgnoreCase(r) || "MODULO_VENTAS".equalsIgnoreCase(r) || "MODULO_CAJA".equalsIgnoreCase(r);
    }

    public boolean hasClientes() {
        if (!isPermisoActivo() || permiso.getRecurso() == null) return false;
        String r = permiso.getRecurso().getNombre();
        return "SISTEMA_TOTAL".equalsIgnoreCase(r) || "MODULO_VENTAS".equalsIgnoreCase(r) || "MODULO_CAJA".equalsIgnoreCase(r);
    }

    public boolean hasProductos() {
        if (!isPermisoActivo() || permiso.getRecurso() == null) return false;
        String r = permiso.getRecurso().getNombre();
        return "SISTEMA_TOTAL".equalsIgnoreCase(r) || "MODULO_INVENTARIO".equalsIgnoreCase(r);
    }

    public boolean hasVentasHistorial() {
        if (!isPermisoActivo() || permiso.getRecurso() == null) return false;
        String r = permiso.getRecurso().getNombre();
        return "SISTEMA_TOTAL".equalsIgnoreCase(r) || "MODULO_VENTAS".equalsIgnoreCase(r) || "MODULO_CAJA".equalsIgnoreCase(r);
    }

    public boolean hasReportes() {
        if (!isPermisoActivo() || permiso.getRecurso() == null) return false;
        String r = permiso.getRecurso().getNombre();
        return "SISTEMA_TOTAL".equalsIgnoreCase(r) || "MODULO_REPORTES".equalsIgnoreCase(r);
    }

    public boolean hasCaja() {
        if (!isPermisoActivo() || permiso.getRecurso() == null) return false;
        String r = permiso.getRecurso().getNombre();
        return "SISTEMA_TOTAL".equalsIgnoreCase(r) || "MODULO_CAJA".equalsIgnoreCase(r);
    }

    public boolean hasConfiguracion() {
        if (!isPermisoActivo() || permiso.getRecurso() == null) return false;
        String r = permiso.getRecurso().getNombre();
        return "SISTEMA_TOTAL".equalsIgnoreCase(r);
    }

    @Override
    public String toString() {
        return "Rol [idrol=" + idrol + ", nombre=" + nombre + ", estado=" + estado + ", permiso=" + permiso + "]";
    }
}