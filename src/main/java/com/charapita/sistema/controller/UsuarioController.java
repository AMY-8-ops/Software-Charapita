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

import com.charapita.sistema.dto.LoginRequestDTO;
import com.charapita.sistema.dto.UsuarioResponseDTO;
import com.charapita.sistema.entity.Usuario;
import com.charapita.sistema.service.IUsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final IUsuarioService serviceUsuario;

    public UsuarioController(IUsuarioService serviceUsuario) {
        this.serviceUsuario = serviceUsuario;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(serviceUsuario.listarTodos());
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = serviceUsuario.guardar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody Usuario usuario) {
        // Validamos que desde Postman nos envíen el ID sí o sí
        if (usuario.getIdusuario() == null) {
            return ResponseEntity.badRequest().body("Error: El ID del usuario es requerido para modificar.");
        }

        try {
            Usuario actualizado = serviceUsuario.actualizar(usuario.getIdusuario(), usuario);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            // Si el DNI choca o el Rol no existe, lo capturamos aquí
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Integer id) {
        try {
            serviceUsuario.eliminarLogico(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<String> eliminarSinId() {
        return ResponseEntity.badRequest()
                .body("Error: El ID es obligatorio en la URL para poder eliminar un registro.");
    }

    /* lOGIN UWU */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            UsuarioResponseDTO response = serviceUsuario.login(loginRequest.getCorreo(), loginRequest.getContrasena());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Devolvemos un 401 Unauthorized si las credenciales son incorrectas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout/{id}")
    public ResponseEntity<?> logout(@PathVariable("id") Integer idusuario) {
        try {
            serviceUsuario.logout(idusuario);
            return ResponseEntity.ok("Sesión cerrada correctamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}