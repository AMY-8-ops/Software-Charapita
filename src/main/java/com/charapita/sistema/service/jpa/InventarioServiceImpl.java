package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.dto.InventarioResponseDTO;
import com.charapita.sistema.entity.Inventario;
import com.charapita.sistema.repository.InventarioRepository;
import com.charapita.sistema.service.IInventarioService;

@Service
public class InventarioServiceImpl implements IInventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioServiceImpl(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> obtenerReporteInventario() {
        return inventarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> obtenerAlertasDeStock() {
        // Reutilizamos el método principal, pero filtramos solo los que tienen alerta
        return obtenerReporteInventario().stream()
                .filter(InventarioResponseDTO::getAlertaStockAbajo)
                .collect(Collectors.toList());
    }

    private InventarioResponseDTO convertirADTO(Inventario i) {
        InventarioResponseDTO dto = new InventarioResponseDTO();
        dto.setIdinventario(i.getIdinventario());
        dto.setIdproducto(i.getProducto().getIdproducto());
        dto.setNombreProducto(i.getProducto().getNombre());
        dto.setCategoria(i.getProducto().getCategoria().getNombre());
        dto.setStockactual(i.getStockactual());
        dto.setStockminimo(i.getStockminimo());
        
        // Lógica de negocio: Calculamos si hay alerta de stock
        dto.setAlertaStockAbajo(i.getStockactual() <= i.getStockminimo());
        
        return dto;
    }
}