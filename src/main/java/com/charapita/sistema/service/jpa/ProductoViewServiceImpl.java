package com.charapita.sistema.service.jpa;

import com.charapita.sistema.dto.ProductoDashboardDTO;
import com.charapita.sistema.entity.Categoria;
import com.charapita.sistema.entity.Inventario;
import com.charapita.sistema.entity.Merma;
import com.charapita.sistema.entity.MotivoMerma;
import com.charapita.sistema.entity.Presentacion;
import com.charapita.sistema.entity.Producto;
import com.charapita.sistema.repository.CategoriaRepository;
import com.charapita.sistema.repository.InventarioRepository;
import com.charapita.sistema.repository.MermaRepository;
import com.charapita.sistema.repository.MotivoMermaRepository;
import com.charapita.sistema.repository.PresentacionRepository;
import com.charapita.sistema.repository.ProductoRepository;
import com.charapita.sistema.service.IProductoViewService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class ProductoViewServiceImpl implements IProductoViewService {

    private final InventarioRepository inventarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final PresentacionRepository presentacionRepository;
    private final ProductoRepository productoRepository;
    private final MotivoMermaRepository motivoMermaRepository;
    private final MermaRepository mermaRepository;

    public ProductoViewServiceImpl(InventarioRepository inventarioRepository,
                                   CategoriaRepository categoriaRepository,
                                   PresentacionRepository presentacionRepository,
                                   ProductoRepository productoRepository,
                                   MotivoMermaRepository motivoMermaRepository,
                                   MermaRepository mermaRepository) {
        this.inventarioRepository = inventarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.presentacionRepository = presentacionRepository;
        this.productoRepository = productoRepository;
        this.motivoMermaRepository = motivoMermaRepository;
        this.mermaRepository = mermaRepository;
    }

    @Override
    public ProductoDashboardDTO getProductoDashboardData() {
        ProductoDashboardDTO dto = new ProductoDashboardDTO();

        List<Inventario> inventarios = inventarioRepository.findAll().stream()
                .filter(i -> Boolean.TRUE.equals(i.getEstado())
                        && Boolean.TRUE.equals(i.getProducto().getEstado()))
                .toList();

        List<Categoria> categorias = categoriaRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .toList();

        List<Presentacion> presentaciones = presentacionRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getEstado()))
                .toList();

        List<Producto> productos = productoRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getEstado()))
                .toList();

        List<MotivoMerma> motivos = motivoMermaRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getEstado()))
                .toList();

        long totalProductos = productos.size();

        java.math.BigDecimal valorInventario = inventarios.stream()
                .map(i -> {
                    java.math.BigDecimal price = i.getProducto().getPrecio() != null
                            ? i.getProducto().getPrecio()
                            : java.math.BigDecimal.ZERO;
                    int stock = i.getStockactual() != null ? i.getStockactual() : 0;
                    return price.multiply(java.math.BigDecimal.valueOf(stock));
                })
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        long productosAlerta = inventarios.stream()
                .filter(i -> i.getStockactual() != null && i.getStockminimo() != null
                        && i.getStockactual() <= i.getStockminimo())
                .count();

        List<Merma> allMermas = mermaRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getEstado()))
                .toList();

        java.math.BigDecimal valorMermas = allMermas.stream()
                .map(m -> {
                    java.math.BigDecimal price = m.getProducto() != null && m.getProducto().getPrecio() != null
                            ? m.getProducto().getPrecio()
                            : java.math.BigDecimal.ZERO;
                    int qty = m.getCantidad() != null ? m.getCantidad() : 0;
                    return price.multiply(java.math.BigDecimal.valueOf(qty));
                })
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        List<Merma> ultimasMermas = allMermas.stream()
                .sorted(Comparator.comparing(Merma::getFechahora, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();

        String fechaActual = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        dto.setInventarios(inventarios);
        dto.setCategorias(categorias);
        dto.setPresentaciones(presentaciones);
        dto.setProductos(productos);
        dto.setMotivos(motivos);
        dto.setTotalProductos(totalProductos);
        dto.setValorInventario(valorInventario);
        dto.setProductosAlerta(productosAlerta);
        dto.setValorMermas(valorMermas);
        dto.setUltimasMermas(ultimasMermas);
        dto.setFechaActual(fechaActual);

        return dto;
    }
}
