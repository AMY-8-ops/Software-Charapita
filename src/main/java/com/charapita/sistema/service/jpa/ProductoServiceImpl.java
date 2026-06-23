package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.dto.ProductoResponseDTO;
import com.charapita.sistema.entity.Categoria;
import com.charapita.sistema.entity.Presentacion;
import com.charapita.sistema.entity.Producto;
import com.charapita.sistema.repository.CategoriaRepository;
import com.charapita.sistema.repository.PresentacionRepository;
import com.charapita.sistema.repository.ProductoRepository;
import com.charapita.sistema.service.IProductoService;

@Service
public class ProductoServiceImpl implements IProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final PresentacionRepository presentacionRepository;

    // Inyectamos los 3 repositorios en el constructor
    public ProductoServiceImpl(ProductoRepository productoRepository, 
                               CategoriaRepository categoriaRepository, 
                               PresentacionRepository presentacionRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.presentacionRepository = presentacionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarTodosActivos() {
        return productoRepository.findAll().stream()
                .filter(Producto::getEstado) // Solo listamos los que tienen estado = true
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Producto guardar(Producto producto) {
        // --- VALIDACIÓN DE CATEGORÍA ---
        if (producto.getCategoria() == null || producto.getCategoria().getIdcategoria() == null) {
            throw new IllegalArgumentException("Error: El campo 'idcategoria' es obligatorio.");
        }
        Integer idCat = producto.getCategoria().getIdcategoria();
        Categoria catBD = categoriaRepository.findById(idCat)
                .orElseThrow(() -> new IllegalArgumentException("Error: La Categoría con ID [" + idCat + "] no existe."));
        if (catBD.getEstado() != null && !catBD.getEstado()) {
            throw new IllegalArgumentException("Error: La Categoría con ID [" + idCat + "] está inactiva.");
        }

        // --- VALIDACIÓN DE PRESENTACIÓN ---
        if (producto.getPresentacion() == null || producto.getPresentacion().getIdpresentacion() == null) {
            throw new IllegalArgumentException("Error: El campo 'idpresentacion' es obligatorio.");
        }
        Integer idPres = producto.getPresentacion().getIdpresentacion();
        Presentacion presBD = presentacionRepository.findById(idPres)
                .orElseThrow(() -> new IllegalArgumentException("Error: La Presentación con ID [" + idPres + "] no existe."));
        if (presBD.getEstado() != null && !presBD.getEstado()) {
            throw new IllegalArgumentException("Error: La Presentación con ID [" + idPres + "] está inactiva.");
        }

        producto.setEstado(true); // Activo por defecto al crear
        return productoRepository.save(producto);
    }

    // --- NUEVO: MÉTODO ACTUALIZAR (PATCHING) ---
    @Override
    @Transactional
    public Producto actualizar(Integer id, Producto productoRecibido) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (productoRecibido.getNombre() != null) existente.setNombre(productoRecibido.getNombre());
        if (productoRecibido.getPrecio() != null) existente.setPrecio(productoRecibido.getPrecio());

        // Validamos la Categoría si intentan cambiarla
        if (productoRecibido.getCategoria() != null && productoRecibido.getCategoria().getIdcategoria() != null) {
            Integer idCat = productoRecibido.getCategoria().getIdcategoria();
            Categoria catBD = categoriaRepository.findById(idCat)
                    .orElseThrow(() -> new IllegalArgumentException("Error: La Categoría con ID [" + idCat + "] no existe."));
            if (catBD.getEstado() != null && !catBD.getEstado()) {
                throw new IllegalArgumentException("Error: La Categoría está inactiva.");
            }
            existente.setCategoria(catBD);
        }

        // Validamos la Presentación si intentan cambiarla
        if (productoRecibido.getPresentacion() != null && productoRecibido.getPresentacion().getIdpresentacion() != null) {
            Integer idPres = productoRecibido.getPresentacion().getIdpresentacion();
            Presentacion presBD = presentacionRepository.findById(idPres)
                    .orElseThrow(() -> new IllegalArgumentException("Error: La Presentación con ID [" + idPres + "] no existe."));
            if (presBD.getEstado() != null && !presBD.getEstado()) {
                throw new IllegalArgumentException("Error: La Presentación está inactiva.");
            }
            existente.setPresentacion(presBD);
        }

        if (productoRecibido.getEstado() != null) existente.setEstado(productoRecibido.getEstado());

        return productoRepository.save(existente);
    }

    @Override
    @Transactional
    public void cambiarEstado(Integer id, boolean estado) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        existente.setEstado(estado); // Para borrado lógico o reactivación
        productoRepository.save(existente);
    }

    // --- Métodos Privados ---
    private ProductoResponseDTO convertirADTO(Producto p) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setIdproducto(p.getIdproducto());
        dto.setNombre(p.getNombre());
        dto.setPrecio(p.getPrecio());
        
        dto.setCategoria(p.getCategoria() != null ? p.getCategoria().getNombre() : "Sin Categoría");
        dto.setPresentacion(p.getPresentacion() != null ? p.getPresentacion().getDescripcion() : "Sin Presentación");
        
        return dto;
    }
}