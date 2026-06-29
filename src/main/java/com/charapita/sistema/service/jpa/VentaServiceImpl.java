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

        // Validar regla de SUNAT (Ventas mayores a S/ 700.00 requieren identificar al cliente, no se permite Cliente Ocasional / ID 8)
        java.math.BigDecimal totalVenta = java.math.BigDecimal.ZERO;
        if (dto.getDetalles() != null) {
            for (DetalleVentaRequestDTO det : dto.getDetalles()) {
                if (det.getImporte() != null) {
                    totalVenta = totalVenta.add(det.getImporte());
                }
            }
        }
        if (totalVenta.compareTo(new java.math.BigDecimal("700")) > 0) {
            if (cliente.getIdcliente() == 8 || 
                "Cliente Ocasional".equalsIgnoreCase(cliente.getNombre()) || 
                "Cliente Ocasional".equalsIgnoreCase(cliente.getRazonsocial()) || 
                "00000000".equals(cliente.getNroDocumento())) {
                throw new IllegalArgumentException("Por regulación de la SUNAT, las ventas que superan los S/ 700.00 requieren obligatoria e inexcusablemente la identificación del cliente (no se permite Cliente Ocasional).");
            }
        }

        Usuario usuario = usuarioRepository.findById(dto.getIdusuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));
        TipoComprobante comprobante = tipoComprobanteRepository.findById(dto.getIdtipocomprobante())
                .orElseThrow(() -> new IllegalArgumentException("Comprobante inválido"));
        MetodoPago metodoPago = metodoPagoRepository.findById(dto.getIdmetodopago())
                .orElseThrow(() -> new IllegalArgumentException("Método de pago inválido"));

        // Validar código de verificación según el método de pago en el backend
        if (metodoPago.getIdmetodo() != 1 && !metodoPago.getNombre().toLowerCase().contains("efectivo")) {
            String codigo = dto.getNroOperacion();
            if (codigo == null || codigo.trim().isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar el código de comprobación / operación bancaria.");
            }
            codigo = codigo.trim();
            String nameLower = metodoPago.getNombre().toLowerCase();
            
            if (metodoPago.getIdmetodo() == 2 || (nameLower.contains("yape") && nameLower.contains("plin")) || nameLower.contains("presencial")) {
                if (!codigo.matches("^\\d{3}$")) {
                    throw new IllegalArgumentException("El Código de Verificación Dinámico debe ser un número de exactamente 3 dígitos.");
                }
            } else if (metodoPago.getIdmetodo() == 5 || nameLower.contains("e-commerce") || nameLower.contains("pasarela")) {
                if (!codigo.matches("^\\d{6}$")) {
                    throw new IllegalArgumentException("El Código de Aprobación de Compra debe ser un número de exactamente 6 dígitos.");
                }
            } else if (metodoPago.getIdmetodo() == 4 || nameLower.contains("tarjeta") || nameLower.contains("pos") || nameLower.contains("niubiz") || nameLower.contains("izipay")) {
                if (!codigo.matches("^[a-zA-Z0-9]{4,8}$")) {
                    throw new IllegalArgumentException("El Número de Operación / Autorización debe ser alfanumérico y tener entre 4 y 8 caracteres.");
                }
            } else if (metodoPago.getIdmetodo() == 3 || nameLower.contains("transferencia") || nameLower.contains("depósito") || nameLower.contains("deposito")) {
                if (!codigo.matches("^[a-zA-Z0-9]+$")) {
                    throw new IllegalArgumentException("El Número de Operación Bancaria debe ser alfanumérico.");
                }
            }
        }

        // 2. Crear y guardar la Venta (Cabecera)
        Venta venta = new Venta();
        
        // Generar nroPedido correlativo basado en la serie y tipo de comprobante
        String serie = dto.getNroPedido();
        if (serie == null || !serie.contains("-")) {
            String prefix = (comprobante.getIdtipo() == 1) ? "B001" : "F001";
            serie = prefix + "-AUTO";
        }
        
        String[] parts = serie.split("-");
        String prefix = parts[0].toUpperCase().trim();
        
        // Validación de formato de Serie (exactamente 4 caracteres, empezando con B o F)
        if (prefix.length() != 4) {
            throw new IllegalArgumentException("La serie del comprobante debe tener exactamente 4 caracteres.");
        }
        if (comprobante.getIdtipo() == 1 && !prefix.startsWith("B")) {
            throw new IllegalArgumentException("Para Boleta, la serie debe comenzar con la letra B.");
        }
        if (comprobante.getIdtipo() == 2 && !prefix.startsWith("F")) {
            throw new IllegalArgumentException("Para Factura, la serie debe comenzar con la letra F.");
        }

        // Buscar en la BD la venta con el correlativo máximo para este prefijo
        List<Venta> todas = ventaRepository.findAll();
        int maxCorrelativo = 0;
        for (Venta v : todas) {
            if (v.getNroPedido() != null && v.getNroPedido().startsWith(prefix + "-")) {
                String[] p = v.getNroPedido().split("-");
                if (p.length == 2) {
                    try {
                        int num = Integer.parseInt(p[1]);
                        if (num > maxCorrelativo) {
                            maxCorrelativo = num;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        
        String nroPedidoFinal = prefix + "-" + String.format("%06d", maxCorrelativo + 1);
        venta.setNroPedido(nroPedidoFinal);
        venta.setFecha(LocalDateTime.now());
        venta.setEstado(true);
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setTipoComprobante(comprobante);
        venta.setMetodoPago(metodoPago);
        venta.setNroOperacion(dto.getNroOperacion() != null ? dto.getNroOperacion().trim() : null);

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
        dto.setNroOperacion(venta.getNroOperacion());
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