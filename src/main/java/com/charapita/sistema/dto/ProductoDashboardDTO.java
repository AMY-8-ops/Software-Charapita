package com.charapita.sistema.dto;

import java.math.BigDecimal;
import java.util.List;
import com.charapita.sistema.entity.Categoria;
import com.charapita.sistema.entity.Inventario;
import com.charapita.sistema.entity.Merma;
import com.charapita.sistema.entity.MotivoMerma;
import com.charapita.sistema.entity.Presentacion;
import com.charapita.sistema.entity.Producto;

public class ProductoDashboardDTO {
    private List<Inventario> inventarios;
    private List<Categoria> categorias;
    private List<Presentacion> presentaciones;
    private List<Producto> productos;
    private List<MotivoMerma> motivos;
    private long totalProductos;
    private BigDecimal valorInventario;
    private long productosAlerta;
    private BigDecimal valorMermas;
    private List<Merma> ultimasMermas;
    private String fechaActual;

    public ProductoDashboardDTO() {}

    public List<Inventario> getInventarios() { return inventarios; }
    public void setInventarios(List<Inventario> inventarios) { this.inventarios = inventarios; }

    public List<Categoria> getCategorias() { return categorias; }
    public void setCategorias(List<Categoria> categorias) { this.categorias = categorias; }

    public List<Presentacion> getPresentaciones() { return presentaciones; }
    public void setPresentaciones(List<Presentacion> presentaciones) { this.presentaciones = presentaciones; }

    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }

    public List<MotivoMerma> getMotivos() { return motivos; }
    public void setMotivos(List<MotivoMerma> motivos) { this.motivos = motivos; }

    public long getTotalProductos() { return totalProductos; }
    public void setTotalProductos(long totalProductos) { this.totalProductos = totalProductos; }

    public BigDecimal getValorInventario() { return valorInventario; }
    public void setValorInventario(BigDecimal valorInventario) { this.valorInventario = valorInventario; }

    public long getProductosAlerta() { return productosAlerta; }
    public void setProductosAlerta(long productosAlerta) { this.productosAlerta = productosAlerta; }

    public BigDecimal getValorMermas() { return valorMermas; }
    public void setValorMermas(BigDecimal valorMermas) { this.valorMermas = valorMermas; }

    public List<Merma> getUltimasMermas() { return ultimasMermas; }
    public void setUltimasMermas(List<Merma> ultimasMermas) { this.ultimasMermas = ultimasMermas; }

    public String getFechaActual() { return fechaActual; }
    public void setFechaActual(String fechaActual) { this.fechaActual = fechaActual; }
}
