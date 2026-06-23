package com.charapita.sistema.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.charapita.sistema.dto.IngresoRequestDTO;
import com.charapita.sistema.entity.IngresoProduccion;
import com.charapita.sistema.service.IIngresoProduccionService;

@RestController
@RequestMapping("/api/ingresos")
public class IngresoProduccionController {

    private final IIngresoProduccionService serviceIngreso;

    public IngresoProduccionController(IIngresoProduccionService serviceIngreso) {
        this.serviceIngreso = serviceIngreso;
    }

    // 1. LISTAR TODOS (GET http://localhost:8080/api/ingresos)
    @GetMapping
    public ResponseEntity<List<IngresoProduccion>> listarTodos() {
        return ResponseEntity.ok(serviceIngreso.listarTodos());
    }

    // 2. BUSCAR POR ID CON CONTROL DE ERROR (GET http://localhost:8080/api/ingresos/{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable("id") Integer id) {
        Optional<IngresoProduccion> ingreso = serviceIngreso.buscarPorId(id);
        
        if (ingreso.isPresent()) {
            return ResponseEntity.ok(ingreso.get());
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Ingreso no encontrado");
    }

    // 3. REGISTRAR INGRESO MAESTRO-DETALLE (POST http://localhost:8080/api/ingresos)
    @PostMapping
    public ResponseEntity<?> registrarIngreso(@RequestBody IngresoRequestDTO dto) {
        try {
            IngresoProduccion nuevoIngreso = serviceIngreso.registrarIngreso(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoIngreso);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error en los datos del ingreso: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ocurrió un error inesperado procesando el ingreso");
        }
    }

    // 4. ACTUALIZAR CABECERA CONTROLADO (PUT http://localhost:8080/api/ingresos/{id})
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarIngreso(@PathVariable("id") Integer id, @RequestBody IngresoRequestDTO dto) {
        try {
            // Capturamos el objeto devuelto por el servicio
            IngresoProduccion actualizado = serviceIngreso.actualizar(id, dto);
            
            // Lo enviamos en el body en lugar del texto plano
            return ResponseEntity.ok(actualizado);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al actualizar el ingreso");
        }
    }

    // 5. MÉTODO TRAMPA: Evita el error 405 si el cliente olvida pasar el ID en la URL del PUT
    @PutMapping
    public ResponseEntity<?> actualizarSinId() {
        return ResponseEntity.badRequest().body("Error: El ID del ingreso es obligatorio en la URL para poder modificarlo.");
    }

    // 6. ELIMINAR CONTROLADO (DELETE http://localhost:8080/api/ingresos/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarIngreso(@PathVariable("id") Integer id) {
        try {
            serviceIngreso.eliminar(id);
            return ResponseEntity.ok().body("Ingreso eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al eliminar el ingreso");
        }
    }

    // 7. MÉTODO TRAMPA: Evita el error 405 si el cliente olvida pasar el ID en la URL del DELETE
    @DeleteMapping
    public ResponseEntity<?> eliminarSinId() {
        return ResponseEntity.badRequest().body("Error: El ID del ingreso es obligatorio en la URL para poder eliminarlo.");
    }
}