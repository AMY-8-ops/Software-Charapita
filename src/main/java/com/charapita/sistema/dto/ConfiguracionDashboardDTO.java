package com.charapita.sistema.dto;

import java.util.List;
import com.charapita.sistema.entity.Rol;
import com.charapita.sistema.entity.Usuario;

public class ConfiguracionDashboardDTO {
    private List<Usuario> usuarios;
    private long usuariosActivos;
    private long totalUsuarios;
    private long totalRoles;
    private long totalPermisos;
    private String ultimoUsuario;
    private String fechaUltimoAcceso;
    private List<Rol> roles;

    public ConfiguracionDashboardDTO() {}

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }

    public long getUsuariosActivos() { return usuariosActivos; }
    public void setUsuariosActivos(long usuariosActivos) { this.usuariosActivos = usuariosActivos; }

    public long getTotalUsuarios() { return totalUsuarios; }
    public void setTotalUsuarios(long totalUsuarios) { this.totalUsuarios = totalUsuarios; }

    public long getTotalRoles() { return totalRoles; }
    public void setTotalRoles(long totalRoles) { this.totalRoles = totalRoles; }

    public long getTotalPermisos() { return totalPermisos; }
    public void setTotalPermisos(long totalPermisos) { this.totalPermisos = totalPermisos; }

    public String getUltimoUsuario() { return ultimoUsuario; }
    public void setUltimoUsuario(String ultimoUsuario) { this.ultimoUsuario = ultimoUsuario; }

    public String getFechaUltimoAcceso() { return fechaUltimoAcceso; }
    public void setFechaUltimoAcceso(String fechaUltimoAcceso) { this.fechaUltimoAcceso = fechaUltimoAcceso; }

    public List<Rol> getRoles() { return roles; }
    public void setRoles(List<Rol> roles) { this.roles = roles; }
}
