package com.charapita.sistema.dto;

import java.util.List;
import com.charapita.sistema.entity.Categoria;
import com.charapita.sistema.entity.Inventario;
import com.charapita.sistema.entity.MetodoPago;

public class NuevaVentaDTO {
    private List<Inventario> inventarios;
    private List<Categoria> categorias;
    private List<MetodoPago> metodos;

    public NuevaVentaDTO() {}

    public List<Inventario> getInventarios() { return inventarios; }
    public void setInventarios(List<Inventario> inventarios) { this.inventarios = inventarios; }

    public List<Categoria> getCategorias() { return categorias; }
    public void setCategorias(List<Categoria> categorias) { this.categorias = categorias; }

    public List<MetodoPago> getMetodos() { return metodos; }
    public void setMetodos(List<MetodoPago> metodos) { this.metodos = metodos; }
}
