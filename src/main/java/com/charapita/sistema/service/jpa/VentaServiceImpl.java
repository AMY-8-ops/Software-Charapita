package com.charapita.sistema.service.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.dto.VentaRequestDTO;
import com.charapita.sistema.dto.VentaResponseDTO;
import com.charapita.sistema.dto.DetalleVentaRequestDTO;
import com.charapita.sistema.dto.DetalleVentaResponseDTO;
import com.charapita.sistema.entity.*;
import com.charapita.sistema.repository.*;
import com.charapita.sistema.service.IVentaService;

@Service
public class VentaServiceImpl implements IVentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoComprobanteRepository tipoComprobanteRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    public VentaServiceImpl(VentaRepository ventaRepository, DetalleVentaRepository detalleVentaRepository,
                            ClienteRepository clienteRepository, UsuarioRepository usuarioRepository,
                            TipoComprobanteRepository tipoComprobanteRepository, MetodoPagoRepository metodoPagoRepository,
                            ProductoRepository productoRepository, InventarioRepository inventarioRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.tipoComprobanteRepository = tipoComprobanteRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    @Transactional // CRÍTICO: Si falla un detalle, la venta completa se cancela
    public VentaResponseDTO registrarVenta(VentaRequestDTO dto) {
        
        // 1. Buscar las relaciones de la cabecera
        Cliente cliente = clienteRepository.findById(dto.getIdcliente())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no existe"));
        Usuario usuario = usuarioRepository.findById(dto.getIdusuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));
        TipoComprobante comprobante = tipoComprobanteRepository.findById(dto.getIdtipocomprobante())
                .orElseThrow(() -> new IllegalArgumentException("Comprobante inválido"));
        MetodoPago metodoPago = metodoPagoRepository.findById(dto.getIdmetodopago())
                .orElseThrow(() -> new IllegalArgumentException("Método de pago inválido"));

        // 2. Crear y guardar la Venta (Cabecera)
        Venta venta = new Venta();
        // Generamos un nro de pedido aleatorio de 6 caracteres
        venta.setNroPedido(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        venta.setFecha(LocalDateTime.now());
        venta.setEstado(true);
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setTipoComprobante(comprobante);
        venta.setMetodoPago(metodoPago);

        // Al hacer save, la venta ya tiene su 'idventa' autogenerado
        Venta ventaGuardada = ventaRepository.save(venta);

        // 3. Procesar y guardar los Detalles
        for (DetalleVentaRequestDTO detalleDTO : dto.getDetalles()) {
            
            Producto producto = productoRepository.findById(detalleDTO.getIdproducto())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no existe."));

            if (producto.getEstado() == null || !producto.getEstado()) {
                throw new IllegalArgumentException("El producto [" + producto.getNombre() + "] está inactivo/desactivado.");
            }

            // Validar stock en el inventario antes de proceder
            Inventario inventario = inventarioRepository.findByProducto_Idproducto(producto.getIdproducto())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró registro de inventario para el producto: " + producto.getNombre()));

            if (inventario.getEstado() != null && !inventario.getEstado()) {
                throw new IllegalArgumentException("El inventario del producto [" + producto.getNombre() + "] está inactivo/desactivado.");
            }

            if (inventario.getStockactual() == null || inventario.getStockactual() < detalleDTO.getCantidad()) {
                int disponible = inventario.getStockactual() == null ? 0 : inventario.getStockactual();
                throw new IllegalArgumentException("Stock insuficiente para el producto [" + producto.getNombre() + "]. Stock disponible: " + disponible + ", solicitado: " + detalleDTO.getCantidad());
            }

            DetalleVenta detalle = new DetalleVenta();
            
            // Aquí configuramos la llave compuesta!
            DetalleVentaId idCompuesto = new DetalleVentaId();
            idCompuesto.setIdventa(ventaGuardada.getIdventa());
            idCompuesto.setIdproducto(producto.getIdproducto());
            detalle.setId(idCompuesto);

            detalle.setVenta(ventaGuardada);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioU(detalleDTO.getPrecioU());
            detalle.setImporte(detalleDTO.getImporte());

            detalleVentaRepository.save(detalle);
            // Al guardar, se dispara tu Trigger en la BD y actualiza el inventario.
        }
        return convertirADTO(ventaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listarTodas() {
        return ventaRepository.findByEstadoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VentaResponseDTO> buscarPorId(Integer id) {
        return ventaRepository.findById(id)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional
    public void anularVenta(Integer id) {
        Venta existente = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada."));

        if (existente.getEstado() != null && !existente.getEstado()) {
            throw new IllegalArgumentException("Esta venta ya se encuentra anulada.");
        }

        // 1. Borrado lógico de la venta
        existente.setEstado(false);
        ventaRepository.save(existente);

        // 2. Obtener los detalles de la venta
        List<DetalleVenta> detalles = detalleVentaRepository.findByIdIdventa(id);

        // 3. Devolución de stock de cada producto
        for (DetalleVenta d : detalles) {
            Inventario inventario = inventarioRepository.findByProducto_Idproducto(d.getProducto().getIdproducto())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró inventario para el producto: " + d.getProducto().getNombre()));

            inventario.setStockactual(inventario.getStockactual() + d.getCantidad());
            inventarioRepository.save(inventario);
        }
    }

    private VentaResponseDTO convertirADTO(Venta venta) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setIdventa(venta.getIdventa());
        dto.setNroPedido(venta.getNroPedido());
        dto.setDireccion(venta.getDireccion());
        dto.setFecha(venta.getFecha());
        dto.setEstado(venta.getEstado());
        dto.setClienteNombre(venta.getCliente() != null ? venta.getCliente().getNombre() : null);
        dto.setUsuarioNombre(venta.getUsuario() != null ? venta.getUsuario().getNombre() : null);
        dto.setTipoComprobanteDescripcion(venta.getTipoComprobante() != null ? venta.getTipoComprobante().getNombre() : null);
        dto.setMetodoPagoDescripcion(venta.getMetodoPago() != null ? venta.getMetodoPago().getNombre() : null);

        // Buscar los detalles de esta venta
        List<DetalleVentaResponseDTO> detalles = detalleVentaRepository.findByIdIdventa(venta.getIdventa()).stream()
                .map(d -> {
                    DetalleVentaResponseDTO dd = new DetalleVentaResponseDTO();
                    dd.setIdproducto(d.getProducto().getIdproducto());
                    dd.setNombreProducto(d.getProducto().getNombre());
                    dd.setCantidad(d.getCantidad());
                    dd.setPrecioU(d.getPrecioU());
                    dd.setImporte(d.getImporte());
                    return dd;
                })
                .collect(Collectors.toList());
        dto.setDetalles(detalles);
        return dto;
    }
}