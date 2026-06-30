package com.charapita.sistema.dto;

public class RolPermisosDTO {
    private Boolean modNuevaVenta;
    private Boolean modClientes;
    private Boolean modProductos;
    private Boolean modVentasHistorial;
    private Boolean modReportes;
    private Boolean modCaja;
    private Boolean modConfiguracion;

    public Boolean getModNuevaVenta() { return modNuevaVenta; }
    public void setModNuevaVenta(Boolean modNuevaVenta) { this.modNuevaVenta = modNuevaVenta; }

    public Boolean getModClientes() { return modClientes; }
    public void setModClientes(Boolean modClientes) { this.modClientes = modClientes; }

    public Boolean getModProductos() { return modProductos; }
    public void setModProductos(Boolean modProductos) { this.modProductos = modProductos; }

    public Boolean getModVentasHistorial() { return modVentasHistorial; }
    public void setModVentasHistorial(Boolean modVentasHistorial) { this.modVentasHistorial = modVentasHistorial; }

    public Boolean getModReportes() { return modReportes; }
    public void setModReportes(Boolean modReportes) { this.modReportes = modReportes; }

    public Boolean getModCaja() { return modCaja; }
    public void setModCaja(Boolean modCaja) { this.modCaja = modCaja; }

    public Boolean getModConfiguracion() { return modConfiguracion; }
    public void setModConfiguracion(Boolean modConfiguracion) { this.modConfiguracion = modConfiguracion; }
}
