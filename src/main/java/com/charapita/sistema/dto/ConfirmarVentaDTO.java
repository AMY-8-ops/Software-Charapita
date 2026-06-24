package com.charapita.sistema.dto;

import java.util.List;
import com.charapita.sistema.entity.Cliente;
import com.charapita.sistema.entity.MetodoPago;
import com.charapita.sistema.entity.TipoComprobante;
import com.charapita.sistema.entity.Usuario;

public class ConfirmarVentaDTO {
    private List<Cliente> clientes;
    private List<Usuario> usuarios;
    private List<TipoComprobante> comprobantes;
    private List<MetodoPago> metodos;

    public ConfirmarVentaDTO() {}

    public List<Cliente> getClientes() { return clientes; }
    public void setClientes(List<Cliente> clientes) { this.clientes = clientes; }

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }

    public List<TipoComprobante> getComprobantes() { return comprobantes; }
    public void setComprobantes(List<TipoComprobante> comprobantes) { this.comprobantes = comprobantes; }

    public List<MetodoPago> getMetodos() { return metodos; }
    public void setMetodos(List<MetodoPago> metodos) { this.metodos = metodos; }
}
