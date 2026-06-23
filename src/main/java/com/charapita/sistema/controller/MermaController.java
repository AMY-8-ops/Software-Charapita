package com.charapita.sistema.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charapita.sistema.dto.MermaRequestDTO;
import com.charapita.sistema.dto.MermaResponseDTO;
import com.charapita.sistema.service.IMermaService;

@RestController
@RequestMapping("/api/mermas")
public class MermaController {

    private final IMermaService serviceMerma;

    public MermaController(IMermaService serviceMerma) {
        this.serviceMerma = serviceMerma;
    }

    @GetMapping
    public ResponseEntity<List<MermaResponseDTO>> listarHistorial() {
        return ResponseEntity.ok(serviceMerma.listarHistorial());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable("id") Integer id) {
        Optional<MermaResponseDTO> mermaOpt = serviceMerma.buscarPorId(id);
        if (mermaOpt.isPresent()) {
            return ResponseEntity.ok(mermaOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Merma no encontrada");
    }

    @PostMapping
    public ResponseEntity<?> registrarMerma(@RequestBody MermaRequestDTO dto) {
        try {
            serviceMerma.registrarMerma(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Merma registrada exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error en los datos de la merma: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error inesperado al registrar la merma: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable("id") Integer id) {
        try {
            serviceMerma.eliminar(id);
            return ResponseEntity.ok("Merma anulada exitosamente y stock devuelto.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al anular la merma");
        }
    }

    @DeleteMapping
    public ResponseEntity<String> eliminarSinId() {
        return ResponseEntity.badRequest().body("Error: El ID de la merma es obligatorio en la URL para poder anularla.");
    }
}