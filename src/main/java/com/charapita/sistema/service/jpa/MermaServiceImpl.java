package com.charapita.sistema.service.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.dto.MermaRequestDTO;
import com.charapita.sistema.dto.MermaResponseDTO;
import com.charapita.sistema.entity.Inventario;
import com.charapita.sistema.entity.Merma;
import com.charapita.sistema.entity.MotivoMerma;
import com.charapita.sistema.entity.Producto;
import com.charapita.sistema.repository.InventarioRepository;
import com.charapita.sistema.repository.MermaRepository;
import com.charapita.sistema.repository.MotivoMermaRepository;
import com.charapita.sistema.repository.ProductoRepository;
import com.charapita.sistema.service.IMermaService;

@Service
public class MermaServiceImpl implements IMermaService {

    private final MermaRepository mermaRepository;
    private final ProductoRepository productoRepository;
    private final MotivoMermaRepository motivoRepository;
    private final InventarioRepository inventarioRepository;

    public MermaServiceImpl(MermaRepository mermaRepository, ProductoRepository productoRepository, MotivoMermaRepository motivoRepository, InventarioRepository inventarioRepository) {
        this.mermaRepository = mermaRepository;
        this.productoRepository = productoRepository;
        this.motivoRepository = motivoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MermaResponseDTO> listarHistorial() {
        return mermaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void registrarMerma(MermaRequestDTO dto) {
        // 1. Validar que el DTO y los datos básicos no sean nulos
        if (dto == null) {
            throw new IllegalArgumentException("Los datos de la merma son obligatorios.");
        }
        if (dto.getIdproducto() == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio.");
        }
        if (dto.getIdmotivo() == null) {
            throw new IllegalArgumentException("El ID del motivo de merma es obligatorio.");
        }
        if (dto.getCantidad() == null) {
            throw new IllegalArgumentException("La cantidad de la merma es obligatoria.");
        }
        if (dto.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad de la merma debe ser mayor a cero.");
        }

        // 2. Validar existencia y estado del producto
        Producto producto = productoRepository.findById(dto.getIdproducto())
                .orElseThrow(() -> new IllegalArgumentException("El producto seleccionado con ID [" + dto.getIdproducto() + "] no existe."));
        if (producto.getEstado() == null || !producto.getEstado()) {
            throw new IllegalArgumentException("El producto [" + producto.getNombre() + "] se encuentra inactivo/desactivado.");
        }

        // 3. Validar existencia y estado del motivo de merma
        MotivoMerma motivo = motivoRepository.findById(dto.getIdmotivo())
                .orElseThrow(() -> new IllegalArgumentException("El motivo de merma seleccionado con ID [" + dto.getIdmotivo() + "] no existe."));
        if (motivo.getEstado() == null || !motivo.getEstado()) {
            throw new IllegalArgumentException("El motivo de merma [" + motivo.getDescripcion() + "] se encuentra inactivo/desactivado.");
        }

        // 4. Validar existencia de inventario, su estado y stock suficiente
        Inventario inventario = inventarioRepository.findByProducto_Idproducto(dto.getIdproducto())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un registro de inventario para el producto [" + producto.getNombre() + "]."));
        
        if (inventario.getEstado() != null && !inventario.getEstado()) {
            throw new IllegalArgumentException("El inventario para el producto [" + producto.getNombre() + "] se encuentra inactivo/desactivado.");
        }

        if (inventario.getStockactual() == null || inventario.getStockactual() < dto.getCantidad()) {
            int stockDisponible = inventario.getStockactual() == null ? 0 : inventario.getStockactual();
            throw new IllegalArgumentException("Stock insuficiente para registrar la merma. Stock disponible: " + stockDisponible + ", cantidad solicitada: " + dto.getCantidad());
        }

        // 5. Crear y guardar la merma
        Merma merma = new Merma();
        merma.setFechahora(LocalDateTime.now());
        merma.setCantidad(dto.getCantidad());
        merma.setProducto(producto);
        merma.setMotivoMerma(motivo);
        merma.setEstado(true);

        mermaRepository.save(merma);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MermaResponseDTO> buscarPorId(Integer id) {
        return mermaRepository.findById(id)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Merma existente = mermaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Merma no encontrada"));

        if (existente.getEstado() != null && !existente.getEstado()) {
            throw new IllegalArgumentException("Esta merma ya se encuentra anulada.");
        }

        // Borrado lógico
        existente.setEstado(false);
        mermaRepository.save(existente);

        // Devolución del stock
        Inventario inventario = inventarioRepository.findByProducto_Idproducto(existente.getProducto().getIdproducto())
                .orElseThrow(() -> new IllegalArgumentException("Inventario del producto no encontrado"));

        inventario.setStockactual(inventario.getStockactual() + existente.getCantidad());
        inventarioRepository.save(inventario);
    }

    private MermaResponseDTO convertirADTO(Merma m) {
        MermaResponseDTO dto = new MermaResponseDTO();
        dto.setIdmerma(m.getIdmerma());
        dto.setFechahora(m.getFechahora());
        dto.setCantidad(m.getCantidad());
        dto.setProductoNombre(m.getProducto().getNombre());
        dto.setMotivoDescripcion(m.getMotivoMerma().getDescripcion());
        dto.setEstado(m.getEstado());
        return dto;
    }
}