package com.charapita.sistema.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charapita.sistema.dto.ClienteDTO;
import com.charapita.sistema.service.IClienteService;
import com.charapita.sistema.repository.VentaRepository;
import com.charapita.sistema.repository.DetalleVentaRepository;
import com.charapita.sistema.entity.Venta;
import com.charapita.sistema.entity.DetalleVenta;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final IClienteService serviceCliente;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public ClienteController(IClienteService serviceCliente, VentaRepository ventaRepository, DetalleVentaRepository detalleVentaRepository) {
        this.serviceCliente = serviceCliente;
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }


    @GetMapping
    public ResponseEntity<List<ClienteDTO>> buscarTodos() {
        return ResponseEntity.ok(serviceCliente.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Integer id) {
        try {
            return ResponseEntity.ok(serviceCliente.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody ClienteDTO dto) {
        try {
            ClienteDTO nuevoCliente = serviceCliente.guardar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody ClienteDTO dto) {
        if (dto.getIdcliente() == null) {
            return ResponseEntity.badRequest().body("El ID es requerido para modificar");
        }
        try {
            ClienteDTO actualizado = serviceCliente.actualizar(dto.getIdcliente(), dto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Integer id) {
        try {
            serviceCliente.eliminar(id);
            return ResponseEntity.ok("Cliente eliminado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping
    public ResponseEntity<String> eliminarSinId() {
        return ResponseEntity.badRequest().body("Error: El ID es obligatorio en la URL para poder eliminar un registro.");
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<?> obtenerHistorial(@PathVariable("id") Integer id) {
        try {
            ClienteDTO clientDto = serviceCliente.buscarPorId(id);
            
            List<Venta> sales = ventaRepository.findAll().stream()
                    .filter(v -> Boolean.TRUE.equals(v.getEstado()))
                    .filter(v -> v.getCliente() != null && v.getCliente().getIdcliente().equals(id))
                    .sorted((v1, v2) -> v2.getFecha().compareTo(v1.getFecha()))
                    .toList();
            
            List<Map<String, Object>> compras = new ArrayList<>();
            Map<String, Map<String, Object>> productStats = new HashMap<>();
            
            for (Venta v : sales) {
                List<DetalleVenta> details = detalleVentaRepository.findByIdIdventa(v.getIdventa());
                BigDecimal total = details.stream()
                        .map(d -> d.getImporte() != null ? d.getImporte() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                Map<String, Object> compra = new HashMap<>();
                compra.put("idventa", v.getIdventa());
                compra.put("fecha", v.getFecha() != null ? v.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "---");
                compra.put("comprobante", v.getNroPedido() != null ? v.getNroPedido() : "---");
                compra.put("tipo", v.getTipoComprobante() != null ? v.getTipoComprobante().getNombre() : "Boleta");
                compra.put("total", total);
                compra.put("metodo", v.getMetodoPago() != null ? v.getMetodoPago().getNombre() : "Efectivo");
                compra.put("vendedor", v.getUsuario() != null ? (v.getUsuario().getNombre() + " " + (v.getUsuario().getApellido() != null ? v.getUsuario().getApellido() : "")) : "Sistema");
                
                compras.add(compra);
                
                for (DetalleVenta d : details) {
                    if (d.getProducto() != null) {
                        String prodName = d.getProducto().getNombre();
                        String unit = d.getProducto().getPresentacion() != null ? d.getProducto().getPresentacion().getDescripcion() : "unidades";
                        int qty = d.getCantidad() != null ? d.getCantidad() : 0;
                        BigDecimal importe = d.getImporte() != null ? d.getImporte() : BigDecimal.ZERO;
                        
                        productStats.putIfAbsent(prodName, new HashMap<>());
                        Map<String, Object> stat = productStats.get(prodName);
                        stat.put("nombre", prodName);
                        stat.put("unidad", unit);
                        stat.put("totalQty", (int)stat.getOrDefault("totalQty", 0) + qty);
                        stat.put("purchaseCount", (int)stat.getOrDefault("purchaseCount", 0) + 1);
                        BigDecimal prevImporte = (BigDecimal) stat.getOrDefault("totalImporte", BigDecimal.ZERO);
                        stat.put("totalImporte", prevImporte.add(importe));
                    }
                }
            }
            
            List<Map<String, Object>> prefProducts = new ArrayList<>();
            for (Map<String, Object> stat : productStats.values()) {
                double avgQty = (double)((int)stat.get("totalQty")) / (int)stat.get("purchaseCount");
                double avgQtyRounded = Math.round(avgQty * 10.0) / 10.0;
                
                Map<String, Object> pref = new HashMap<>();
                pref.put("nombre", stat.get("nombre"));
                pref.put("unidad", stat.get("unidad"));
                pref.put("promedioCantidad", avgQtyRounded);
                pref.put("totalQty", stat.get("totalQty"));
                pref.put("totalImporte", stat.get("totalImporte"));
                prefProducts.add(pref);
            }
            
            prefProducts.sort((p1, p2) -> {
                BigDecimal i1 = (BigDecimal) p1.get("totalImporte");
                BigDecimal i2 = (BigDecimal) p2.get("totalImporte");
                return i2.compareTo(i1);
            });
            
            if (prefProducts.size() > 3) {
                prefProducts = prefProducts.subList(0, 3);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("cliente", clientDto);
            response.put("compras", compras);
            response.put("productosPreferidos", prefProducts);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}