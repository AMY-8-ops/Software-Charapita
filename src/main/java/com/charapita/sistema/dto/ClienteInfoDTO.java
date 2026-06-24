package com.charapita.sistema.dto;

import com.charapita.sistema.entity.Cliente;
import java.math.BigDecimal;

public class ClienteInfoDTO {
    private final Cliente cliente;
    private final String telefono;
    private final String correo;
    private final String ultimaCompra;
    private final BigDecimal totalConsumido;

    public ClienteInfoDTO(Cliente cliente, String telefono, String correo, String ultimaCompra,
                    BigDecimal totalConsumido) {
            this.cliente = cliente;
            this.telefono = telefono;
            this.correo = correo;
            this.ultimaCompra = ultimaCompra;
            this.totalConsumido = totalConsumido;
    }

    public Cliente getCliente() {
            return cliente;
    }

    public String getTelefono() {
            return telefono;
    }

    public String getCorreo() {
            return correo;
    }

    public String getUltimaCompra() {
            return ultimaCompra;
    }

    public BigDecimal getTotalConsumido() {
            return totalConsumido;
    }
}
