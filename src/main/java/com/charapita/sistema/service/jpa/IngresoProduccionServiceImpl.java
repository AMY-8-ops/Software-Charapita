package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.dto.DetalleIngresoRequestDTO;
import com.charapita.sistema.dto.IngresoRequestDTO;
import com.charapita.sistema.entity.DetalleIngreso;
import com.charapita.sistema.entity.DetalleIngresoId;
import com.charapita.sistema.entity.IngresoProduccion;
import com.charapita.sistema.entity.Producto;
import com.charapita.sistema.repository.DetalleIngresoRepository;
import com.charapita.sistema.repository.IngresoProduccionRepository;
import com.charapita.sistema.repository.ProductoRepository;
import com.charapita.sistema.service.IIngresoProduccionService;

@Service
public class IngresoProduccionServiceImpl implements IIngresoProduccionService {

    private final IngresoProduccionRepository ingresoRepository;
    private final DetalleIngresoRepository detalleIngresoRepository;
    private final ProductoRepository productoRepository;

    public IngresoProduccionServiceImpl(IngresoProduccionRepository ingresoRepository, DetalleIngresoRepository detalleIngresoRepository, ProductoRepository productoRepository) {
        this.ingresoRepository = ingresoRepository;
        this.detalleIngresoRepository = detalleIngresoRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional 
    public IngresoProduccion registrarIngreso(IngresoRequestDTO dto) { 
        // 1. Guardar la Cabecera
        IngresoProduccion ingreso = new IngresoProduccion();
        ingreso.setDniResponsable(dto.getDniResponsable());
        ingreso.setNombreResponsable(dto.getNombreResponsable());
        ingreso.setDetalle(dto.getDetalle());
        
        IngresoProduccion ingresoGuardado = ingresoRepository.save(ingreso);

        // 2. Iterar y guardar los Detalles
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El ingreso debe tener al menos un producto en el detalle.");
        }

        for (DetalleIngresoRequestDTO detalleDTO : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getIdproducto())
                    .orElseThrow(() -> new IllegalArgumentException("Error: El Producto con ID [" + detalleDTO.getIdproducto() + "] no existe."));

            DetalleIngreso detalle = new DetalleIngreso();
            DetalleIngresoId idCompuesto = new DetalleIngresoId();
            idCompuesto.setIdingreso(ingresoGuardado.getIdingreso());
            idCompuesto.setIdproducto(producto.getIdproducto());
            
            detalle.setId(idCompuesto);
            detalle.setIngresoProduccion(ingresoGuardado);
            detalle.setProducto(producto);
            detalle.setNroLote(detalleDTO.getNroLote());
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setFechaVencimiento(detalleDTO.getFechaVencimiento());

            detalleIngresoRepository.save(detalle);
        }

        return ingresoGuardado; 
    }

    // NUEVO: Método para actualizar (retornando el objeto modificado)
    @Override
    @Transactional
    public IngresoProduccion actualizar(Integer id, IngresoRequestDTO dto) {
        IngresoProduccion existente = ingresoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado en el sistema."));

        if (dto.getDniResponsable() != null) existente.setDniResponsable(dto.getDniResponsable());
        if (dto.getNombreResponsable() != null) existente.setNombreResponsable(dto.getNombreResponsable());
        if (dto.getDetalle() != null) existente.setDetalle(dto.getDetalle());

        // Retornamos el objeto guardado
        return ingresoRepository.save(existente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngresoProduccion> listarTodos() {
        return ingresoRepository.findByEstadoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IngresoProduccion> buscarPorId(Integer id) {
        return ingresoRepository.findByIdingresoAndEstadoTrue(id);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        // 1. Verificar existencia y que esté activo
        IngresoProduccion existente = ingresoRepository.findByIdingresoAndEstadoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado o ya eliminado."));

        // 2. Borrado lógico: Cambiar estado a false
        existente.setEstado(false);

        // 3. Guardar cambios en cabecera
        ingresoRepository.save(existente);
    }
}