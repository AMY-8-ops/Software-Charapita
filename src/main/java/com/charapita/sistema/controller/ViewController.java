package com.charapita.sistema.controller;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import com.charapita.sistema.service.IVentaService;
import com.charapita.sistema.service.IMermaService;
import com.charapita.sistema.dto.VentaResponseDTO;
import com.charapita.sistema.dto.MermaResponseDTO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.charapita.sistema.entity.Categoria;
import com.charapita.sistema.entity.Cliente;
import com.charapita.sistema.entity.DetalleVenta;
import com.charapita.sistema.entity.Inventario;
import com.charapita.sistema.entity.Merma;
import com.charapita.sistema.entity.MotivoMerma;
import com.charapita.sistema.entity.Presentacion;
import com.charapita.sistema.entity.Producto;
import com.charapita.sistema.entity.Rol;
import com.charapita.sistema.entity.TipoCliente;
import com.charapita.sistema.entity.Usuario;
import com.charapita.sistema.entity.Venta;
import com.charapita.sistema.repository.CategoriaRepository;
import com.charapita.sistema.repository.ClienteRepository;
import com.charapita.sistema.repository.DetalleVentaRepository;
import com.charapita.sistema.repository.InventarioRepository;
import com.charapita.sistema.repository.MermaRepository;
import com.charapita.sistema.repository.MotivoMermaRepository;
import com.charapita.sistema.repository.PresentacionRepository;
import com.charapita.sistema.repository.ProductoRepository;
import com.charapita.sistema.repository.RolRepository;
import com.charapita.sistema.repository.TipoClienteRepository;
import com.charapita.sistema.repository.UsuarioRepository;
import com.charapita.sistema.repository.VentaRepository;
import com.charapita.sistema.entity.Caja;
import com.charapita.sistema.entity.MovimientoCaja;
import com.charapita.sistema.entity.TipoComprobante;
import com.charapita.sistema.entity.MetodoPago;
import com.charapita.sistema.repository.CajaRepository;
import com.charapita.sistema.repository.MovimientoCajaRepository;
import com.charapita.sistema.repository.TipoComprobanteRepository;
import com.charapita.sistema.repository.MetodoPagoRepository;

@Controller
public class ViewController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final PresentacionRepository presentacionRepository;
    private final MermaRepository mermaRepository;
    private final MotivoMermaRepository motivoMermaRepository;
    private final ClienteRepository clienteRepository;
    private final TipoClienteRepository tipoClienteRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final CajaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final TipoComprobanteRepository tipoComprobanteRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final IVentaService serviceVenta;
    private final IMermaService serviceMerma;

    public ViewController(UsuarioRepository usuarioRepository, RolRepository rolRepository,
            InventarioRepository inventarioRepository,
            ProductoRepository productoRepository, CategoriaRepository categoriaRepository,
            PresentacionRepository presentacionRepository, MermaRepository mermaRepository,
            MotivoMermaRepository motivoMermaRepository, ClienteRepository clienteRepository,
            TipoClienteRepository tipoClienteRepository, VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository, CajaRepository cajaRepository,
            MovimientoCajaRepository movimientoCajaRepository, TipoComprobanteRepository tipoComprobanteRepository,
            MetodoPagoRepository metodoPagoRepository, IVentaService serviceVenta, IMermaService serviceMerma) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.presentacionRepository = presentacionRepository;
        this.mermaRepository = mermaRepository;
        this.motivoMermaRepository = motivoMermaRepository;
        this.clienteRepository = clienteRepository;
        this.tipoClienteRepository = tipoClienteRepository;
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.cajaRepository = cajaRepository;
        this.movimientoCajaRepository = movimientoCajaRepository;
        this.tipoComprobanteRepository = tipoComprobanteRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.serviceVenta = serviceVenta;
        this.serviceMerma = serviceMerma;
    }


    public static class ClienteInfo {
        private final Cliente cliente;
        private final String telefono;
        private final String correo;
        private final String ultimaCompra;
        private final java.math.BigDecimal totalConsumido;

        public ClienteInfo(Cliente cliente, String telefono, String correo, String ultimaCompra, java.math.BigDecimal totalConsumido) {
            this.cliente = cliente;
            this.telefono = telefono;
            this.correo = correo;
            this.ultimaCompra = ultimaCompra;
            this.totalConsumido = totalConsumido;
        }

        public Cliente getCliente() { return cliente; }
        public String getTelefono() { return telefono; }
        public String getCorreo() { return correo; }
        public String getUltimaCompra() { return ultimaCompra; }
        public java.math.BigDecimal getTotalConsumido() { return totalConsumido; }
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login.html";
    }

    @GetMapping({ "/login", "/login.html" })
    public String login() {
        return "login";
    }

    @GetMapping({ "/dashboard", "/dashboard.html" })
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping({ "/caja", "/caja/index", "/caja/index.html" })
    public String caja(Model model) {
        MovimientoCaja activeMovimiento = movimientoCajaRepository.findAll().stream()
                .filter(m -> m.getFhCierre() == null)
                .findFirst()
                .orElse(null);

        boolean isAbierta = activeMovimiento != null;
        java.math.BigDecimal totalInicial = java.math.BigDecimal.ZERO;
        java.math.BigDecimal salesEfectivo = java.math.BigDecimal.ZERO;
        java.math.BigDecimal salesElectronicas = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalVentas = java.math.BigDecimal.ZERO;
        java.math.BigDecimal montoEsperado = java.math.BigDecimal.ZERO;
        String nombreCajero = "Ninguno";
        String fechaApertura = "---";
        Integer activeId = null;

        if (isAbierta) {
            activeId = activeMovimiento.getIdmovimiento();
            totalInicial = activeMovimiento.getMontoinicial() != null ? activeMovimiento.getMontoinicial() : java.math.BigDecimal.ZERO;
            if (activeMovimiento.getUsuario() != null) {
                nombreCajero = activeMovimiento.getUsuario().getNombre() + " " + 
                               (activeMovimiento.getUsuario().getApellido() != null ? activeMovimiento.getUsuario().getApellido() : "");
            }
            if (activeMovimiento.getFhApertura() != null) {
                fechaApertura = activeMovimiento.getFhApertura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            }

            final java.time.LocalDateTime openingTime = activeMovimiento.getFhApertura();
            List<Venta> sales = ventaRepository.findAll().stream()
                    .filter(v -> Boolean.TRUE.equals(v.getEstado()))
                    .filter(v -> v.getFecha() != null && !v.getFecha().isBefore(openingTime))
                    .toList();

            for (Venta v : sales) {
                List<DetalleVenta> details = detalleVentaRepository.findByIdIdventa(v.getIdventa());
                java.math.BigDecimal totalSale = details.stream()
                        .map(d -> d.getImporte() != null ? d.getImporte() : java.math.BigDecimal.ZERO)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                
                if (v.getMetodoPago() != null && v.getMetodoPago().getNombre().toLowerCase().contains("efectivo")) {
                    salesEfectivo = salesEfectivo.add(totalSale);
                } else {
                    salesElectronicas = salesElectronicas.add(totalSale);
                }
            }
            totalVentas = salesEfectivo.add(salesElectronicas);
            montoEsperado = totalInicial.add(salesEfectivo);
        }

        List<MovimientoCaja> allMovements = movimientoCajaRepository.findAll().stream()
                .sorted((m1, m2) -> m2.getFhApertura().compareTo(m1.getFhApertura()))
                .toList();

        List<java.util.Map<String, Object>> hist = new java.util.ArrayList<>();
        for (MovimientoCaja mc : allMovements) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("idmovimiento", mc.getIdmovimiento());
            map.put("fechaApertura", mc.getFhApertura() != null ? mc.getFhApertura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "---");
            map.put("fechaCierre", mc.getFhCierre() != null ? mc.getFhCierre().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-");
            map.put("cajero", mc.getUsuario() != null ? (mc.getUsuario().getNombre() + " " + (mc.getUsuario().getApellido() != null ? mc.getUsuario().getApellido() : "")) : "Sistema");
            map.put("montoInicial", mc.getMontoinicial() != null ? mc.getMontoinicial() : java.math.BigDecimal.ZERO);
            map.put("montoFinal", mc.getMontofinal() != null ? mc.getMontofinal() : java.math.BigDecimal.ZERO);
            map.put("diferencia", mc.getDiferencia() != null ? mc.getDiferencia() : java.math.BigDecimal.ZERO);
            map.put("estado", mc.getFhCierre() != null ? "CERRADA" : "ABIERTA");

            java.time.LocalDateTime start = mc.getFhApertura();
            java.time.LocalDateTime end = mc.getFhCierre() != null ? mc.getFhCierre() : java.time.LocalDateTime.now();

            List<Venta> salesInShift = ventaRepository.findAll().stream()
                    .filter(v -> Boolean.TRUE.equals(v.getEstado()))
                    .filter(v -> v.getFecha() != null && !v.getFecha().isBefore(start) && !v.getFecha().isAfter(end))
                    .toList();

            java.math.BigDecimal vef = java.math.BigDecimal.ZERO;
            java.math.BigDecimal vel = java.math.BigDecimal.ZERO;
            for (Venta v : salesInShift) {
                List<DetalleVenta> details = detalleVentaRepository.findByIdIdventa(v.getIdventa());
                java.math.BigDecimal totalSale = details.stream()
                        .map(d -> d.getImporte() != null ? d.getImporte() : java.math.BigDecimal.ZERO)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                
                if (v.getMetodoPago() != null && v.getMetodoPago().getNombre().toLowerCase().contains("efectivo")) {
                    vef = vef.add(totalSale);
                } else {
                    vel = vel.add(totalSale);
                }
            }
            map.put("ventasEfectivo", vef);
            map.put("ventasElectronicas", vel);

            hist.add(map);
        }

        List<Usuario> listUsuarios = usuarioRepository.findAll().stream()
                .filter(u -> !Boolean.TRUE.equals(u.getEliminado()))
                .filter(u -> u.getEstado() != null && u.getEstado() == 1)
                .toList();

        List<Caja> listCajas = cajaRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .toList();

        if (listCajas.isEmpty()) {
            Caja defaultCaja = new Caja();
            defaultCaja.setNombre("Caja Principal");
            defaultCaja.setEstado(true);
            defaultCaja = cajaRepository.save(defaultCaja);
            listCajas = java.util.List.of(defaultCaja);
        }

        model.addAttribute("is_abierta", isAbierta);
        model.addAttribute("active_id", activeId);
        model.addAttribute("nombre_cajero", nombreCajero);
        model.addAttribute("fecha_apertura", fechaApertura);
        model.addAttribute("monto_inicial", totalInicial);
        model.addAttribute("ventas_efectivo", salesEfectivo);
        model.addAttribute("ventas_electronicas", salesElectronicas);
        model.addAttribute("total_ventas", totalVentas);
        model.addAttribute("monto_esperado", montoEsperado);
        model.addAttribute("historial", hist);
        model.addAttribute("usuarios", listUsuarios);
        model.addAttribute("cajas", listCajas);

        return "caja/index";
    }

    @GetMapping({ "/cliente", "/cliente/index", "/cliente/index.html" })
    public String cliente(Model model) {
        List<Cliente> listClientes = clienteRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .toList();

        List<Venta> activeSales = ventaRepository.findAll().stream()
                .filter(v -> Boolean.TRUE.equals(v.getEstado()))
                .toList();

        List<DetalleVenta> details = detalleVentaRepository.findAll();

        List<ClienteInfo> clienteInfos = listClientes.stream()
                .map(c -> {
                    List<Venta> clientSales = activeSales.stream()
                            .filter(v -> v.getCliente() != null && v.getCliente().getIdcliente().equals(c.getIdcliente()))
                            .toList();

                    java.math.BigDecimal total = clientSales.stream()
                            .flatMap(v -> details.stream().filter(d -> d.getVenta().getIdventa().equals(v.getIdventa())))
                            .map(d -> d.getImporte() != null ? d.getImporte() : java.math.BigDecimal.ZERO)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                    String lastPurchase = clientSales.stream()
                            .map(Venta::getFecha)
                            .filter(java.util.Objects::nonNull)
                            .max(Comparator.naturalOrder())
                            .map(f -> f.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .orElse("Sin compras");

                    String doc = c.getNroDocumento();
                    String tel = c.getTelefono();
                    if (tel == null || tel.trim().isEmpty()) {
                        tel = null;
                    }
                    String cleanName = c.getNombre() != null ? c.getNombre() : c.getRazonsocial();
                    String email = cleanName.toLowerCase().trim().replace(" ", ".") + "@gmail.com";

                    return new ClienteInfo(c, tel, email, lastPurchase, total);
                })
                .toList();

        long totalClientes = listClientes.size();
        long clientesNuevos = totalClientes;

        ClienteInfo best = clienteInfos.stream()
                .max(Comparator.comparing(ClienteInfo::getTotalConsumido))
                .orElse(null);

        String mejorClienteName = "Ninguno";
        java.math.BigDecimal mejorClienteTotal = java.math.BigDecimal.ZERO;
        if (best != null && best.getTotalConsumido().compareTo(java.math.BigDecimal.ZERO) > 0) {
            mejorClienteName = best.getCliente().getNombre() != null ? best.getCliente().getNombre() : best.getCliente().getRazonsocial();
            mejorClienteTotal = best.getTotalConsumido();
        }

        List<Long> intervals = new java.util.ArrayList<>();
        java.util.Map<Integer, List<Venta>> salesByClient = activeSales.stream()
                .filter(v -> v.getCliente() != null)
                .collect(java.util.stream.Collectors.groupingBy(v -> v.getCliente().getIdcliente()));

        for (List<Venta> clientSales : salesByClient.values()) {
            if (clientSales.size() > 1) {
                List<java.time.LocalDateTime> dates = clientSales.stream()
                        .map(Venta::getFecha)
                        .filter(java.util.Objects::nonNull)
                        .sorted()
                        .toList();
                for (int i = 0; i < dates.size() - 1; i++) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(dates.get(i), dates.get(i + 1));
                    intervals.add(days);
                }
            }
        }
        double avgFrecuencia = intervals.stream().mapToLong(Long::longValue).average().orElse(15.0);

        List<TipoCliente> tipos = tipoClienteRepository.findAll().stream()
                .filter(tc -> Boolean.TRUE.equals(tc.getEstado()))
                .toList();

        model.addAttribute("clientes", clienteInfos);
        model.addAttribute("tipos", tipos);
        model.addAttribute("total_clientes", totalClientes);
        model.addAttribute("clientes_nuevos", clientesNuevos);
        model.addAttribute("mejor_cliente", mejorClienteName);
        model.addAttribute("total_mejor_cliente", mejorClienteTotal);
        model.addAttribute("frecuencia_promedio", Math.round(avgFrecuencia));

        return "cliente/index";
    }

    @GetMapping({ "/configuracion", "/configuracion/index", "/configuracion/index.html" })
    public String configuracion(Model model) {
        List<Usuario> listUsuarios = usuarioRepository.findAll().stream()
                .filter(u -> !Boolean.TRUE.equals(u.getEliminado()))
                .toList();

        long usuariosActivos = listUsuarios.stream()
                .filter(u -> u.getEstado() != null && u.getEstado() == 1)
                .count();

        long totalUsuarios = listUsuarios.size();
        long totalRoles = rolRepository.count();
        long totalPermisos = 0;

        Usuario ultimoAccesoUser = listUsuarios.stream()
                .filter(u -> u.getUltimoAcceso() != null)
                .max(Comparator.comparing(Usuario::getUltimoAcceso))
                .orElse(null);

        String ultimoUsuario = "Ninguno";
        String fechaUltimoAcceso = "Sin accesos";
        if (ultimoAccesoUser != null) {
            ultimoUsuario = ultimoAccesoUser.getNombre() + " " + ultimoAccesoUser.getApellido();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            fechaUltimoAcceso = ultimoAccesoUser.getUltimoAcceso().format(formatter);
        }

        List<Rol> listRoles = rolRepository.findAll().stream()
                .filter(r -> Boolean.TRUE.equals(r.getEstado()))
                .toList();

        model.addAttribute("usuarios", listUsuarios);
        model.addAttribute("usuarios_activos", usuariosActivos);
        model.addAttribute("total_usuarios", totalUsuarios);
        model.addAttribute("total_roles", totalRoles);
        model.addAttribute("total_modulos", 7);
        model.addAttribute("ultimo_usuario", ultimoUsuario);
        model.addAttribute("fecha_ultimo_acceso", fechaUltimoAcceso);
        model.addAttribute("roles", listRoles);

        return "configuracion/index";
    }

    @GetMapping({ "/historial", "/historial/index", "/historial/index.html" })
    public String historial(Model model) {
        List<Venta> sales = ventaRepository.findAll().stream()
                .sorted((v1, v2) -> {
                    if (v1.getFecha() == null && v2.getFecha() == null) return 0;
                    if (v1.getFecha() == null) return 1;
                    if (v2.getFecha() == null) return -1;
                    return v2.getFecha().compareTo(v1.getFecha());
                })
                .toList();

        List<java.util.Map<String, Object>> histVentas = new java.util.ArrayList<>();
        for (Venta v : sales) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("idventa", v.getIdventa());
            map.put("fecha", v.getFecha() != null ? v.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "---");
            map.put("hora", v.getFecha() != null ? v.getFecha().format(DateTimeFormatter.ofPattern("HH:mm")) : "---");
            map.put("rawFecha", v.getFecha() != null ? v.getFecha().toLocalDate().toString() : "");
            map.put("tipoComprobante", v.getTipoComprobante() != null ? v.getTipoComprobante().getNombre() : "Boleta");
            map.put("numComprobante", v.getNroPedido() != null ? v.getNroPedido() : "---");
            
            String clienteName = "Ocasional";
            String clienteDoc = "00000000";
            if (v.getCliente() != null) {
                clienteName = v.getCliente().getNombre() != null && !v.getCliente().getNombre().isEmpty() ? v.getCliente().getNombre() : v.getCliente().getRazonsocial();
                clienteDoc = v.getCliente().getNroDocumento();
            }
            map.put("clienteNombre", clienteName);
            map.put("clienteDoc", clienteDoc);
            map.put("metodoPago", v.getMetodoPago() != null ? v.getMetodoPago().getNombre() : "Efectivo");
            map.put("vendedor", v.getUsuario() != null ? (v.getUsuario().getNombre() + " " + (v.getUsuario().getApellido() != null ? v.getUsuario().getApellido() : "")) : "Sistema");
            map.put("estado", Boolean.TRUE.equals(v.getEstado()) ? "Completada" : "Anulada");
            map.put("isCompletada", Boolean.TRUE.equals(v.getEstado()));

            List<DetalleVenta> details = detalleVentaRepository.findByIdIdventa(v.getIdventa());
            java.math.BigDecimal total = details.stream()
                    .map(d -> d.getImporte() != null ? d.getImporte() : java.math.BigDecimal.ZERO)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            map.put("total", total);

            histVentas.add(map);
        }

        String fechaInicio = java.time.LocalDate.now().minusMonths(1).toString();
        String fechaFin = java.time.LocalDate.now().toString();

        model.addAttribute("ventas", histVentas);
        model.addAttribute("total_ventas", histVentas.size());
        model.addAttribute("fecha_inicio", fechaInicio);
        model.addAttribute("fecha_fin", fechaFin);

        return "historial/index";
    }

    @GetMapping({ "/nuevaventa", "/nuevaventa/index", "/nuevaventa/index.html" })
    public String nuevaventa(Model model) {
        List<Inventario> inventarios = inventarioRepository.findAll().stream()
                .filter(i -> Boolean.TRUE.equals(i.getEstado()) && Boolean.TRUE.equals(i.getProducto().getEstado()))
                .toList();

        List<Categoria> categorias = categoriaRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .toList();

        List<MetodoPago> metodos = metodoPagoRepository.findAll().stream()
                .filter(mp -> Boolean.TRUE.equals(mp.getEstado()))
                .toList();

        model.addAttribute("inventarios", inventarios);
        model.addAttribute("categorias", categorias);
        model.addAttribute("metodos", metodos);

        return "nuevaventa/index";
    }

    @GetMapping({ "/nuevaventa/confirmar", "/nuevaventa/confirmar.html" })
    public String confirmarVenta(Model model) {
        List<Cliente> clientes = clienteRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .toList();

        List<Usuario> usuarios = usuarioRepository.findAll().stream()
                .filter(u -> !Boolean.TRUE.equals(u.getEliminado()))
                .filter(u -> u.getEstado() != null && u.getEstado() == 1)
                .toList();

        List<TipoComprobante> comprobantes = tipoComprobanteRepository.findAll().stream()
                .filter(tc -> Boolean.TRUE.equals(tc.getEstado()))
                .toList();

        List<MetodoPago> metodos = metodoPagoRepository.findAll().stream()
                .filter(mp -> Boolean.TRUE.equals(mp.getEstado()))
                .toList();

        List<TipoCliente> tipos = tipoClienteRepository.findAll().stream()
                .filter(t -> Boolean.TRUE.equals(t.getEstado()))
                .toList();

        model.addAttribute("clientes", clientes);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("comprobantes", comprobantes);
        model.addAttribute("metodos", metodos);
        model.addAttribute("tipos", tipos);

        return "nuevaventa/confirmar";
    }

    @GetMapping({ "/producto", "/producto/index", "/producto/index.html" })
    public String producto(Model model) {
        List<Inventario> inventarios = inventarioRepository.findAll().stream()
                .filter(i -> Boolean.TRUE.equals(i.getEstado()) && Boolean.TRUE.equals(i.getProducto().getEstado()))
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
                    java.math.BigDecimal price = i.getProducto().getPrecio() != null ? i.getProducto().getPrecio()
                            : java.math.BigDecimal.ZERO;
                    int stock = i.getStockactual() != null ? i.getStockactual() : 0;
                    return price.multiply(java.math.BigDecimal.valueOf(stock));
                })
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        long productosAlerta = inventarios.stream()
                .filter(i -> i.getStockactual() != null && i.getStockminimo() != null
                        && i.getStockactual() <= i.getStockminimo())
                .count();

        List<Merma> allMermas = mermaRepository.findAll();
        java.time.LocalDateTime startOfMonth = java.time.LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        java.math.BigDecimal valorMermas = allMermas.stream()
                .filter(m -> Boolean.TRUE.equals(m.getEstado()) && m.getFechahora() != null
                        && m.getFechahora().isAfter(startOfMonth))
                .map(m -> {
                    java.math.BigDecimal price = m.getProducto().getPrecio() != null ? m.getProducto().getPrecio()
                            : java.math.BigDecimal.ZERO;
                    int qty = m.getCantidad() != null ? m.getCantidad() : 0;
                    return price.multiply(java.math.BigDecimal.valueOf(qty));
                })
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        List<Merma> ultimasMermas = allMermas.stream()
                .filter(m -> Boolean.TRUE.equals(m.getEstado()))
                .sorted(Comparator.comparing(Merma::getFechahora).reversed())
                .limit(5)
                .toList();

        String fechaActual = java.time.LocalDate.now().toString();

        model.addAttribute("inventarios", inventarios);
        model.addAttribute("categorias", categorias);
        model.addAttribute("presentaciones", presentaciones);
        model.addAttribute("productos", productos);
        model.addAttribute("motivos", motivos);

        model.addAttribute("total_productos", totalProductos);
        model.addAttribute("valor_inventario", valorInventario);
        model.addAttribute("productos_alerta", productosAlerta);
        model.addAttribute("valor_mermas", valorMermas);
        model.addAttribute("ultimas_mermas", ultimasMermas);
        model.addAttribute("fecha_actual", fechaActual);

        return "producto/index";
    }

    @GetMapping({ "/reporte", "/reporte/index", "/reporte/index.html" })
    public String reporte(Model model) {
        // Cargar listas reales
        List<VentaResponseDTO> ventas = serviceVenta.listarTodas().stream()
                .filter(v -> Boolean.TRUE.equals(v.getEstado()))
                .sorted((v1, v2) -> v2.getFecha().compareTo(v1.getFecha()))
                .toList();

        List<Merma> mermas = mermaRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getEstado()))
                .sorted((m1, m2) -> m2.getFechahora().compareTo(m1.getFechahora()))
                .toList();

        List<Inventario> inventarios = inventarioRepository.findAll().stream()
                .filter(i -> Boolean.TRUE.equals(i.getEstado()) && Boolean.TRUE.equals(i.getProducto().getEstado()))
                .toList();

        List<Categoria> categorias = categoriaRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .toList();

        List<Usuario> vendedores = usuarioRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getEstado()))
                .toList();

        List<Caja> cajas = cajaRepository.findAll();

        model.addAttribute("ventas", ventas);
        model.addAttribute("mermas", mermas);
        model.addAttribute("inventarios", inventarios);
        model.addAttribute("categorias", categorias);
        model.addAttribute("vendedores", vendedores);
        model.addAttribute("cajas", cajas);

        // Rango de fechas por defecto (último mes)
        model.addAttribute("fecha_inicio", java.time.LocalDate.now().minusMonths(1).toString());
        model.addAttribute("fecha_fin", java.time.LocalDate.now().toString());

        return "reporte/index";
    }

    @GetMapping("/includes/{page}.html")
    public String getInclude(@PathVariable String page) {
        return "includes/" + page;
    }

    @GetMapping({ "/anomalias", "/anomalias/index", "/anomalias/index.html" })
    public String anomalias(Model model) {
        return "anomalias/index";
    }
}
