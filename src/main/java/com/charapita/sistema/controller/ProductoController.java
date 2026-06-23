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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.charapita.sistema.dto.ProductoResponseDTO;
import com.charapita.sistema.entity.Producto;
import com.charapita.sistema.service.IProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final IProductoService serviceProducto;

    public ProductoController(IProductoService serviceProducto) {
        this.serviceProducto = serviceProducto;
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarTodosActivos() {
        return ResponseEntity.ok(serviceProducto.listarTodosActivos());
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Producto producto) {
        try {
            Producto nuevoProducto = serviceProducto.guardar(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody Producto producto) {
        // 1. Validamos que desde Postman nos envíen el ID sí o sí
        if (producto.getIdproducto() == null) {
            return ResponseEntity.badRequest().body("Error: El ID del producto es requerido para modificar.");
        }
        
        try {
            // 2. Llamamos al método actualizar que creamos en el Service
            Producto actualizado = serviceProducto.actualizar(producto.getIdproducto(), producto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            // 3. Si la categoría o presentación no existen/están inactivas, lo atrapamos aquí
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Ruta para desactivar/activar producto usando RequestParam (ej. /api/productos/5/estado?estado=false)
    @PutMapping("/{id}/estado")
    public ResponseEntity<String> cambiarEstado(@PathVariable("id") Integer id, @RequestParam boolean estado) {
        try {
            serviceProducto.cambiarEstado(id, estado);
            return ResponseEntity.ok("Estado del producto actualizado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping
    public ResponseEntity<String> eliminarSinId() {
        return ResponseEntity.badRequest().body("Error: El ID es obligatorio en la URL para poder eliminar un registro.");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable("id") Integer id) {
        try {
            // Intentamos eliminar (o cambiar el estado)
            serviceProducto.cambiarEstado(id, false); // Usa .eliminar(id) si es otra tabla
            return ResponseEntity.ok("Registro eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            // ¡AQUÍ ESTÁ EL CONTROL!
            // Si el servicio no encuentra el ID y lanza el IllegalArgumentException, 
            // lo atrapamos y devolvemos un 400 con tu mensaje personalizado.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}