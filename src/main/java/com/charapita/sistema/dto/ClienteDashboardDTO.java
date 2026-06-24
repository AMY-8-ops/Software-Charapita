package com.charapita.sistema.dto;

import java.math.BigDecimal;
import java.util.List;
import com.charapita.sistema.entity.TipoCliente;

public class ClienteDashboardDTO {
    private List<ClienteInfoDTO> clientes;
    private List<TipoCliente> tipos;
    private long totalClientes;
    private long clientesNuevos;
    private String mejorCliente;
    private BigDecimal totalMejorCliente;
    private double frecuenciaPromedio;

    public ClienteDashboardDTO() {}

    public List<ClienteInfoDTO> getClientes() { return clientes; }
    public void setClientes(List<ClienteInfoDTO> clientes) { this.clientes = clientes; }

    public List<TipoCliente> getTipos() { return tipos; }
    public void setTipos(List<TipoCliente> tipos) { this.tipos = tipos; }

    public long getTotalClientes() { return totalClientes; }
    public void setTotalClientes(long totalClientes) { this.totalClientes = totalClientes; }

    public long getClientesNuevos() { return clientesNuevos; }
    public void setClientesNuevos(long clientesNuevos) { this.clientesNuevos = clientesNuevos; }

    public String getMejorCliente() { return mejorCliente; }
    public void setMejorCliente(String mejorCliente) { this.mejorCliente = mejorCliente; }

    public BigDecimal getTotalMejorCliente() { return totalMejorCliente; }
    public void setTotalMejorCliente(BigDecimal totalMejorCliente) { this.totalMejorCliente = totalMejorCliente; }

    public double getFrecuenciaPromedio() { return frecuenciaPromedio; }
    public void setFrecuenciaPromedio(double frecuenciaPromedio) { this.frecuenciaPromedio = frecuenciaPromedio; }
}
